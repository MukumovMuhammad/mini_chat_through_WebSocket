package com.example.mini_chat_test.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketSendingData(
    val receiver_id: String,
    val text: String
)
