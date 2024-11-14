package com.plcoding.ktorandroidchat.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.ktorandroidchat.domain.model.ChatSocketService
import com.plcoding.ktorandroidchat.domain.model.ChatState
import com.plcoding.ktorandroidchat.domain.model.MessageApiInterface
import com.plcoding.ktorandroidchat.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageApiInterface: MessageApiInterface,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _messageTextState = mutableStateOf("")
    val messageTextState: State<String> = _messageTextState
    private val _chatState = mutableStateOf(ChatState())
    val chatState: State<ChatState> = _chatState

    private val _toastState = MutableStateFlow<String>("")
    val toastState : SharedFlow<String> = _toastState

    fun connectToChat() {
        getAllMessage()
        savedStateHandle.get<String>("userName")?.let { userName ->
            viewModelScope.launch {
                when(val result = chatSocketService.initSession(userName)) {
                    is Resource.Success -> {
                        chatSocketService.observeMessages()
                            .onEach {message ->
                                val newList = chatState.value.messages.toMutableList().apply {
                                    add(0, message)
                                }
                                _chatState.value = chatState.value.copy(messages = newList)
                            }.launchIn(viewModelScope)
                        _toastState.value = "Connected"
                    }
                    is Resource.Error -> {
                        _toastState.emit(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun onMessageChange(message: String) {
        _messageTextState.value = message
    }

    fun disconnect() {
        viewModelScope.launch{ chatSocketService.closeSession() }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageTextState.value.isNotBlank()) {
                val message = messageTextState.value
                chatSocketService.sendMessage(message)
            }
        }
    }

    fun getAllMessage() {
        viewModelScope.launch {
            _chatState.value = chatState.value.copy(isLoading = true)
            val result = messageApiInterface.getMessages()
            _chatState.value = chatState.value.copy(isLoading = false, messages = result)
        }
    }


}