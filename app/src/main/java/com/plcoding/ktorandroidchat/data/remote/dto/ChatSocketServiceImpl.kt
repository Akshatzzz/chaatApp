package com.plcoding.ktorandroidchat.data.remote.dto

import com.plcoding.ktorandroidchat.domain.model.ChatSocketService
import com.plcoding.ktorandroidchat.domain.model.Message
import com.plcoding.ktorandroidchat.util.Resource
import io.ktor.http.cio.websocket.WebSocketSession
import kotlinx.coroutines.flow.Flow
import com.plcoding.ktorandroidchat.util.Resource.Error
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString

class ChatSocketServiceImpl(
    private val client: HttpClient,
) : ChatSocketService {

    private var socketSession: WebSocketSession? = null
    override suspend fun initSession(userName: String): Resource<Unit> {
        return try {
            socketSession = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocket.url}?userName=$userName")
            }
            if (socketSession?.isActive == true) {
                Resource.Success(Unit)
            } else {
                Error("Failed to establish connection")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socketSession?.send(Frame.Text(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            socketSession?.incoming?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val jsonString = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(jsonString)
                    messageDto.toMessage()
                } ?: emptyFlow()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyFlow()
        }
    }

    override suspend fun closeSession() {
        socketSession?.close()
    }
}