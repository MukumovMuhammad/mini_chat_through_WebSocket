package com.example.mini_chat_test.ViewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_chat_test.DataClasses.MessageData
import com.example.mini_chat_test.DataClasses.OnlineUsers

import com.example.mini_chat_test.DataClasses.WebSocketSendingData
import com.example.mini_chat_test.utills.showNotification
import com.example.mini_chat_test.websocket.WebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.Callback

import okhttp3.OkHttpClient
import okhttp3.Request

import okhttp3.Response
import java.io.IOException

class ChatViewModel: ViewModel() {

    private val serverUrl = "https://mini-chat-service-1091763228160.europe-west1.run.app/"
    private val okHttpClient = OkHttpClient()

    var context: Context? = null

    private val _status = MutableStateFlow("Disconnected")
    val status: StateFlow<String> = _status

    private val _login_status = MutableStateFlow("not logged")
    val login_status: StateFlow<String> = _login_status


    private val _UserMessages = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    val UserMessages: StateFlow<Map<Int, List<String>>> = _UserMessages

    private val _userlist = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val userlist: StateFlow<List<Pair<String, Int>>> = _userlist

    private val _onLineUsersIdList = MutableStateFlow<OnlineUsers>(OnlineUsers(emptyList()))
    val onLineUsersIdList : StateFlow<OnlineUsers> = _onLineUsersIdList


    var SelectedUSerID : Int? = null

    init {
        // Observe the flows from the singleton WebSocketManager
        observeWebSocket()
    }

    fun getUsers() {
        val request = Request.Builder()
            .url(serverUrl + "all_users")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val body: String? = response.body?.string()
                println("Response: $body")

                val root = Json.Default.parseToJsonElement(body!!).jsonArray

                val pairs: List<Pair<String, Int>> = root.map { item ->
                    val arr = item.jsonArray
                    arr[0].jsonPrimitive.content to arr[1].jsonPrimitive.int
                }

                _userlist.value = pairs
            }
        })
    }


    //////////////// WebSockets /////////////////////
    private fun observeWebSocket() {
        // This part is correct. It just listens for whatever the manager is doing.
        viewModelScope.launch {
            WebSocketManager.connectionStatus.collect { newStatus ->
                Log.i("Status_changed_TAG", "The status has been changed to $newStatus")
                _status.value = newStatus
            }
        }

        viewModelScope.launch {
            WebSocketManager.messages.collect { message ->

                if (message?.contains("\"type\":\"ping\"") ?: false){
                    Log.i("WebSocketPing", "This is a ping from the server")
//                    WebSocketManager.sendMessage("\"type\":\"ping\"")

                    val json = Json { ignoreUnknownKeys = true }
                    val result = json.decodeFromString<OnlineUsers>(string = message)
                    Log.i("WebSocketPing", "List of online users ${result}")
                    _onLineUsersIdList.value = result


                }
                else{
                    // ... your message handling logic here is fine ...
                    if (message != null) {
                        Log.i("Received Message TAG", "We received a message! $message")
                        val result = Json.Default.decodeFromString<MessageData>(message)
                        val currentMessagesForUser = _UserMessages.value[result.from] ?: emptyList()
                        val updatedMessagesForUser = currentMessagesForUser + "${result.username}: ${result.text}"
                        _UserMessages.value = _UserMessages.value + (result.from to updatedMessagesForUser)

                        if (SelectedUSerID != result.from){
                            Log.i("WebsocketObserve_TAG", "The notification will be sended!")
                            showNotification(context!!, result.username, result.text)
                        }
                    }
                }

            }
        }
    }

    override fun onCleared() {
        // DO NOT disconnect here anymore. The connection should persist.
        super.onCleared()
        Log.i("ChatViewModel", "ViewModel is cleared, but WebSocket connection remains active.")
    }


    fun sendMessage(reciever_id: Int, message: String) {
        if (message.isNotBlank()) {
            val jsonConverter = Json.Default
            val data = WebSocketSendingData(reciever_id.toString(), message)
            val jsonString = jsonConverter.encodeToString(data)

            // Send message through the manager
            WebSocketManager.sendMessage(jsonString)

            // Update local UI state immediately (optimistic update)
            val currentMessagesForUser = _UserMessages.value[reciever_id] ?: emptyList()
            val updatedMessagesForUser = currentMessagesForUser + "You: ${message}"
            _UserMessages.value = _UserMessages.value + (reciever_id to updatedMessagesForUser)
        }
    }







}