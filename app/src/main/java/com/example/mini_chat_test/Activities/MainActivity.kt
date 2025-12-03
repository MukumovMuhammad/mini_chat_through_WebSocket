package com.example.mini_chat_test.Activities

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable


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

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.example.mini_chat_test.components.ChatScreen
import com.example.mini_chat_test.components.UserListScreen
import com.example.mini_chat_test.ui.theme.Mini_chat_testTheme

import com.example.mini_chat_test.utills.getSavedId
import com.example.mini_chat_test.utills.getSavedUsername
import com.example.mini_chat_test.ViewModels.ChatViewModel
import com.example.mini_chat_test.ViewModels.LoginViewModel
import com.example.mini_chat_test.websocket.WebSocketManager
import kotlinx.coroutines.launch


private var TAG = "MAinActivity_TAG"


class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    val selectedId = mutableStateOf<Int?>(null)
    val is_drawerOpen = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        chatViewModel.context = this
        loginViewModel.context = this

        var savedId: Int? = getSavedId(this)
        var savedUsername : String? =
            getSavedUsername(this)


        if(savedId != null){
            Log.i(TAG, "Trying to connect the Socket id : ${savedId}")
            WebSocketManager.startConnection(savedId)
        }

        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
//            if (selectedId.value != null)  viewModel.SelectedUSerID = selectedId.value
            Mini_chat_testTheme {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Text(savedUsername!!, modifier = Modifier.padding(16.dp))
                                HorizontalDivider()
                                NavigationDrawerItem(
                                    label = {Text("Log out") },
                                    selected = false,
                                    onClick = {
//                                        viewModel.LogOut()
                                        recreate()
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
                                        Text(WsStatus)
                                            },
                                    modifier = Modifier.padding(16.dp),

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
                            if (selectedId.value != null) {
                                BackHandler {
                                    selectedId.value = null
                                }
                                ChatScreen(
                                    viewModel,
                                    selectedId.value!!
                                )
                            } else {
                                viewModel.getUsers()
                                val users by viewModel.userlist.collectAsState()

                                if (users != null) {
                                    UserListScreen(
                                        viewModel,
                                        users,
                                        { userId ->
                                            selectedId.value = userId
                                            viewModel.SelectedUSerID = userId
                                            Log.i(TAG, "Selected user ID: $userId")
                                        })
                                } else {
                                    Text("It seems no one has registered yet :(", modifier = Modifier.padding(padding))
                                }

                            }
                        }
                    }

                }
            }
        }

    }
}