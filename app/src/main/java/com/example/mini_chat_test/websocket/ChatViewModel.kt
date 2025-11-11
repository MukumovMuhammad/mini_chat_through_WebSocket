package com.example.mini_chat_test.websocket

import AppWebSocketListener
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class ChatViewModel: ViewModel() {
    private val serverUrl = "ws://192.168.0.121:8080/ws/232131"
    private var webSocketClient : WebSocketClient? = null

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private val _status = MutableStateFlow("Disconnected")
    val status: StateFlow<String> = _status

    private val _login_status = MutableStateFlow("not logged")
    val login_status: StateFlow<String> = _login_status

    init {
        webSocketClient = WebSocketClient(serverUrl, AppWebSocketListener(::onMessageReceived, ::onStatusChanged))
        connect()
    }

    private fun connect(){
        viewModelScope.launch {
            viewModelScope.launch {
                _status.value = "Connecting..."
                webSocketClient?.connect()
            }
        }
    }

    fun login(username: String, client_id: Long){


        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.0.121:8080/login?username=$username?client_id=$client_id")
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
               Log.e("ChatViewModel_TAG", "Error on login: ${e.message}")
                _login_status.value = "failed"
            }

            override fun onResponse(call: Call, response: Response) {
               _login_status.value = "logged"
            }
        })

    }

    fun sendMessage(message: String) {
        if (message.isNotBlank()) {
            webSocketClient?.sendMessage(message)
        }
    }

    private fun onMessageReceived(message: String) {
        // Update UI state on the main thread
        viewModelScope.launch {
            _messages.value = _messages.value + message
        }
    }

    private fun onStatusChanged(newStatus: String) {
        viewModelScope.launch {
            _status.value = newStatus
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient?.disconnect()
    }



}