package com.plcoding.ktorandroidchat.domain.model

data class ChatState (
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
)