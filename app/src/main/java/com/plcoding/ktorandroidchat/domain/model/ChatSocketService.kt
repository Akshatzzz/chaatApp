package com.plcoding.ktorandroidchat.domain.model

import com.plcoding.ktorandroidchat.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {
    suspend fun initSession(
        userName: String
    ): Resource<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object {
        val BASE_URL = "ws://192.168.0.11:8080"
    }

    sealed class Endpoints(val url: String) {
        object ChatSocket : Endpoints("${BASE_URL}/chat-socket")
    }
}