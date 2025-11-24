package com.example.mini_chat_test.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class OnlineUsers(
    val online_users: List<Int>
)
