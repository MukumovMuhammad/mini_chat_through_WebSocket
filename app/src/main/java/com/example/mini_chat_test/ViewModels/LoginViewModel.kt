package com.example.mini_chat_test.ViewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mini_chat_test.DataClasses.EnumUserStatus
import com.example.mini_chat_test.DataClasses.UserDataResponse
import com.example.mini_chat_test.utills.saveUsernamePasswordAndId
import com.example.mini_chat_test.websocket.WebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class LoginViewModel: ViewModel() {

    private val serverUrl = "https://mini-chat-service-1091763228160.europe-west1.run.app/"
    private val okHttpClient = OkHttpClient()

    var context: Context? = null

    private val _status = MutableStateFlow<EnumUserStatus>(EnumUserStatus.DISCONNECTED)
    val status: StateFlow<EnumUserStatus> = _status

    private val _responseData = MutableStateFlow<UserDataResponse>(UserDataResponse(message = "Please Login"))
    val responseData: StateFlow<UserDataResponse> = _responseData



/////// SIGN UP/LOGIN/LOGOUT/////////////////////
    fun signIn(username: String, password: String){

        Log.i("ChatViewModel_TAG", "Trying to login with username: $username and password: $password")

    _status.value = EnumUserStatus.CONNECTING

        val json = """
                {
                    "username": "${username}",
                    "password": "${password}"
                }
            """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())


        val request = Request.Builder()
            .url(serverUrl+"login")
            .post(requestBody)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatViewModel_TAG", "Error on login: ${e.message}")
                _responseData.value = UserDataResponse(message = e.message)
                _status.value = EnumUserStatus.ERROR
            }

            override fun onResponse(call: Call, response: Response) {
                val json = Json { ignoreUnknownKeys = true }
                val result = response.body?.string()?.let { json.decodeFromString<UserDataResponse>(it) }
                Log.i("ChatViewModel_TAG", "Got the response!")
                Log.i("ChatViewModel_TAG", "Login response: ${result}")

                if (response.code != 200) {
                    Log.e("ChatViewModel_TAG", "Login failed with code: ${response.code}")
                    _responseData.value = UserDataResponse(message = "Login failed with code")
                    _status.value = EnumUserStatus.ERROR
                }
                else{
                    if(result?.status == true){
                        Log.i("ChatViewModel_TAG", "Login success")
                        Log.i("ChatViewModel_TAG", "id: ${result.id}")
                        saveUsernamePasswordAndId(context!!, username, password, result.id)
                        WebSocketManager.startConnection(result.id)
                        _responseData.value = result
                        _status.value = EnumUserStatus.CONNECTED
                    }
                    else{
                        _responseData.value = result!!
                        _status.value = EnumUserStatus.ERROR
                    }




                }

            }
        })

    }

    fun signUp(username: String, password: String){

        Log.i("ChatViewModel_TAG", "Trying to Sign Up with username: $username and password: $password")


        _status.value = EnumUserStatus.CONNECTING

        val json = """
                {
                    "username": "${username}",
                    "password": "${password}"
                }
            """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())


        val request = Request.Builder()
            .url(serverUrl+"sign_up")
            .post(requestBody)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatViewModel_TAG", "Error on sign up: ${e.message}")
                _responseData.value = UserDataResponse(message = e.message)
                _status.value = EnumUserStatus.ERROR
            }

            override fun onResponse(call: Call, response: Response) {
                val json = Json { ignoreUnknownKeys = true }
                val result = response.body?.string()?.let { json.decodeFromString<UserDataResponse>(it) }
                Log.i("ChatViewModel_TAG", "Got the response!")
                Log.i("ChatViewModel_TAG", "SignUp response: ${result}")

                if (response.code != 200) {
                    Log.e("ChatViewModel_TAG", "sign up failed with code: ${response.code}")
                    _responseData.value = UserDataResponse(message= "sign up failed")
                    _status.value = EnumUserStatus.ERROR
                }
                else{

                    if(result?.status == true){
                        Log.i("ChatViewModel_TAG", "Login success")
                        Log.i("ChatViewModel_TAG", "id: ${result?.id}")
                        saveUsernamePasswordAndId(context!!, username, password, result.id)
                        WebSocketManager.startConnection(result.id)
                        _responseData.value = result!!
                        _status.value = EnumUserStatus.CONNECTED
                    }
                    else{
                        _responseData.value = result!!
                        _status.value = EnumUserStatus.ERROR
                    }


                }

            }
        })

    }

    fun LogOut(){
        saveUsernamePasswordAndId(context!!, null, null, null)
        WebSocketManager.closeConnection()
        _status.value = EnumUserStatus.DISCONNECTED
    }

    fun resetLoginStatus(){
//        saveUsernamePasswordAndId(context!!, null, null, null)
        _responseData.value = UserDataResponse(message="Please Login!")
        _status.value = EnumUserStatus.DISCONNECTED
    }





}