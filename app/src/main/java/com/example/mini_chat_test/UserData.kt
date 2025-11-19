package com.example.mini_chat_test

import kotlinx.serialization.Serializable

@Serializable
data class UserDataResponse(
    val username: String? = null,
    val id: Int? = null,
    val message: String? = null,
    val status: Boolean? = null
)
