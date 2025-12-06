package com.example.mini_chat_test.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem

import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import com.example.mini_chat_test.DataClasses.EnumUserStatus
import com.example.mini_chat_test.components.ChatScreen
import com.example.mini_chat_test.components.UserListScreen
import com.example.mini_chat_test.ui.theme.Mini_chat_testTheme

import com.example.mini_chat_test.utills.getSavedId
import com.example.mini_chat_test.utills.getSavedUsername
import com.example.mini_chat_test.ViewModels.ChatViewModel
import com.example.mini_chat_test.ViewModels.LoginViewModel
import com.example.mini_chat_test.websocket.WebSocketManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private var TAG = "MAinActivity_TAG"


class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    val is_drawerOpen = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        chatViewModel.context = this
        loginViewModel.context = this

        var isExitBtnCliked = false



        var savedId: Int? = getSavedId(this)
        var savedUsername : String? =
            getSavedUsername(this)


        if(savedId != null){
            Log.i(TAG, "Trying to connect the Socket id : ${savedId}")
            WebSocketManager.startConnection(savedId)
        }

        setContent {

            val websocketStatus  = chatViewModel.WebSocketStatus.collectAsState()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val users by chatViewModel.userlist.collectAsState()
            val selectedUserId by chatViewModel.selectedUserId.collectAsState()



            LaunchedEffect(isExitBtnCliked) {
                Log.i(TAG, "the launch effect of isCLickedOnce is called with value $isExitBtnCliked")
                if (isExitBtnCliked){
                    delay(1000)
                    isExitBtnCliked = false
                }
            }
            Mini_chat_testTheme {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet (
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ){
                                Text(savedUsername!!, modifier = Modifier.padding(16.dp))
                                HorizontalDivider()
                                NavigationDrawerItem(
                                    label = {Text("Log out") },
                                    selected = false,
                                    onClick = {
                                        loginViewModel.LogOut()
                                        finish()
                                    }
                                )

                                // ...other drawer items
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {

                                        val titleText = when (websocketStatus.value) {
                                            EnumUserStatus.DISCONNECTED -> "Disconnected"
                                            EnumUserStatus.CONNECTING -> "Connecting..."
                                            EnumUserStatus.CONNECTED -> {
                                                if (selectedUserId == null) {
                                                    "Mini Chat"
                                                } else {
                                                    getTheNameOfPair(users, selectedUserId!!)
                                                }
                                            }
                                            EnumUserStatus.ERROR -> "Error"
                                        }

                                        Text(
                                            text = titleText.toString(),
                                            modifier = Modifier.padding(10.dp,0.dp),
                                        )


                                    },
                                    navigationIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            modifier = Modifier.clickable(
                                                onClick = {
                                                    scope.launch {
                                                        drawerState.open()
                                                    }
                                                }
                                            )
                                        )
                                    }

                                )

                            }
                        ) { padding ->

                            Column(modifier = Modifier.padding(padding)) {

                                UserListScreen(chatViewModel)

                                /*
                                if (selectedId.value != null) {
                                    BackHandler {
                                        chatViewModel.SelectedUSerID = null
                                        selectedId.value = null
                                    }
                                    ChatScreen(
                                        chatViewModel,
                                        selectedId.value!!
                                    )
                                } else {
                                    BackHandler {
                                        if (isExitBtnCliked){
                                            Log.i(TAG, "The main activity is about to be finished")
                                            finish()
                                        }

                                        Toast.makeText(this@MainActivity, "Press one again to exit", Toast.LENGTH_LONG).show()
                                        isExitBtnCliked = true
                                    }
                                    chatViewModel.getUsers()

                                    if (!users.isNullOrEmpty()) {
                                        if (users.size > 1){
                                            UserListScreen(
                                                chatViewModel,
                                                users
                                            ) { userId ->
                                                selectedId.value = userId
                                                chatViewModel.SelectedUSerID = userId
                                                Log.i(TAG, "Selected user ID: $userId")
                                            }
                                        }
                                        else{
                                            Text("It seems you are the only user for now :(", modifier = Modifier.padding(padding))
                                        }

                                    } else {
                                        Text("Something went wrong try to refresh the page :(", modifier = Modifier.padding(padding))
                                    }

                                }

                                */
                            }

                        }
                    }

                }
            }
        }
}



fun getTheNameOfPair(data: List<Pair<String, Int>>, id: Int): String?{
    var i : String? = null
    for (item in data){
        if (item.second == id){
           i =  item.first
        }
    }

    return i
}