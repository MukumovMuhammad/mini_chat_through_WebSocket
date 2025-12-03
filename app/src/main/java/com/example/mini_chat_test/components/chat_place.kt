package com.example.mini_chat_test.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mini_chat_test.utills.getSavedId
import com.example.mini_chat_test.ViewModels.ChatViewModel
import kotlinx.coroutines.delay


@Composable
fun UserListScreen(
    viewModel: ChatViewModel,
    users: List<Pair<String, Int>>,   // (username, userId)
    onUserSelected: (Int) -> Unit,
) {
    val context = LocalContext.current
//    var isLoading by remember { mutableStateOf(false) }

    val onlineUsers by viewModel.onlineUsersIdList.collectAsState()
//    val pullToRefreshState = rememberPullToRefreshState()

//    LaunchedEffect(isLoading) {
//        if (isLoading) {
//            delay(1000)
//            isLoading = false
//        }
//    }

//    PullToRefreshBox(
//        modifier = Modifier.fillMaxSize(),
//        state = pullToRefreshState,
//        isRefreshing = isLoading,
//        onRefresh = {
//            isLoading = true
//            viewModel.getUsers()
//        }
//    ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users) { user ->
                    val currentUserId = getSavedId(context)
                    if (user.second != currentUserId) {
                        UserCard(
                            username = user.first,
                            isOnline = onlineUsers.online_users.contains(user.second),
                            onClick = { onUserSelected(user.second) }
                        )
                    }
                }
            }
}


@Composable
fun UserCard(
    username: String,
    isOnline: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F4F4)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /** Avatar */
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF6200EE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.first().uppercase(),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            /** Username + status dot */
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = username,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Online/offline status dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isOnline) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}


@Composable
fun ChatScreen(viewModel: ChatViewModel, selectedId: Int) {

    val messages by viewModel.UserMessages.collectAsState()
    var inputMessage by remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp, 8.dp)) {

        Spacer(modifier = Modifier.height(4.dp))

        val messagesForList: List<String> = messages[selectedId] ?: emptyList()

        LazyColumn(
            modifier = Modifier.weight(2f).fillMaxWidth().padding(vertical = 10.dp),
            reverseLayout = true // Show latest message at the bottom
        ) {
            items(   messagesForList.reversed()){ message ->
                Text(text=message)
            }
        }

        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier.weight(1f),
                label = { Text("Enter message") }
            )
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    viewModel.sendMessage(selectedId, inputMessage)
                    inputMessage = ""
                },
            ) {
                Text("Send")
            }
        }
    }
}
