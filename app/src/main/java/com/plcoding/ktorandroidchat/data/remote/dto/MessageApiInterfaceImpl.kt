package com.plcoding.ktorandroidchat.data.remote.dto

import com.plcoding.ktorandroidchat.domain.model.Message
import com.plcoding.ktorandroidchat.domain.model.MessageApiInterface
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class MessageApiInterfaceImpl(
    private val client: HttpClient
) : MessageApiInterface {
    override suspend fun getMessages(): List<Message> {
        return try {
            client.get<List<MessageDto>>(MessageApiInterface.Endpoints.GetAllMessages.url)
                .map { it.toMessage() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}