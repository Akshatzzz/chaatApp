package com.plcoding.ktorandroidchat.presentation.chat

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    userName: String?,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.toastState.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_START) {
                viewModel.connectToChat()
            } else if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state = viewModel.chatState.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            items(state.messages) { message ->
               val isOwnMessage = message.username == userName
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isOwnMessage) {
                        androidx.compose.ui.Alignment.CenterEnd
                    } else {
                        androidx.compose.ui.Alignment.CenterStart
                    }
                ) {
                   Column(
                       modifier = Modifier.width(200.dp)
                           .drawBehind {
                           val cornerRadius = 10.dp.toPx()
                           val triangleHeight = 20.dp.toPx()
                           val triangleWidth = 25.dp.toPx()

                       }
                   ) {
                        Text(
                            text = message.username,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(text = message.text, color = Color.White)
                        Text(
                            text = message.formattedTime,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}