package com.example.mini_chat_test.Activities

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.example.mini_chat_test.components.ChatScreen
import com.example.mini_chat_test.components.UserListScreen
import com.example.mini_chat_test.ui.theme.Mini_chat_testTheme
import com.example.mini_chat_test.utills.LoadingDialog
import com.example.mini_chat_test.utills.getSavedId
import com.example.mini_chat_test.utills.getSavedUsername
import com.example.mini_chat_test.websocket.ChatViewModel
import com.example.mini_chat_test.websocket.WebSocketManager
import kotlinx.coroutines.launch


private var TAG = "MAinActivity_TAG"


class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    val selectedId = mutableStateOf<Int?>(null)
    val is_drawerOpen = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        viewModel.context = this

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
            val status by viewModel.login_status.collectAsState()
            val WsStatus by viewModel.status.collectAsState()
//            if (selectedId.value != null)  viewModel.SelectedUSerID = selectedId.value
            Mini_chat_testTheme {
                if (status != "success" && savedId == null){
                    LoginScreen(viewModel)
                }
                else {
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
                                        viewModel.LogOut()
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
                                    title = {Text(WsStatus)},
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


@Composable
fun LoginScreen(viewModel: ChatViewModel) {


    var inputUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isloading by remember {mutableStateOf(false)}
    val status by viewModel.login_status.collectAsState()

    isloading = status == "Connecting"
    LoadingDialog(isloading, "connecting")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Status: $status",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

//        Username
        OutlinedTextField(
            value = inputUsername,
            onValueChange = { inputUsername = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

//        Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                Log.i(TAG, "Trying to login!")
                viewModel.login(inputUsername, password)
            }
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                Log.i(TAG, "Trying to Sign Up!")
                viewModel.SignUp(inputUsername, password)
            }
        ) {
            Text("Sign Up")
        }
    }
}
