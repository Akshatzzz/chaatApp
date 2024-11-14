package com.plcoding.ktorandroidchat.domain.model

interface MessageApiInterface {
    suspend fun getMessages(): List<Message>

    companion object {
        val BASE_URL = "http://192.168.0.11:8080"
    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages : Endpoints("${BASE_URL}/messages")
    }
}