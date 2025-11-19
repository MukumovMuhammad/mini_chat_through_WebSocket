import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mini_chat_test.Message
import com.example.mini_chat_test.websocket.ChatViewModel
import kotlinx.serialization.json.Json

@Composable
fun ChatScreen(viewModel: ChatViewModel, my_id: Int) {
    val messages by viewModel.messages.collectAsState()
    val status by viewModel.status.collectAsState()
    var inputMessage by remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Status: $status", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(2f).fillMaxWidth().padding(vertical = 10.dp),
            reverseLayout = true // Show latest message at the bottom
        ) {
            items(messages.reversed()) { message ->
//                Log.i(TAG, "Here we have message: $message")

                val data = Json.decodeFromString<Message>(message)
                val sender = if (data.from == my_id) "You" else data.username

                Text(text = sender + ": " + data.text, modifier = Modifier.padding(2.dp))
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
                    viewModel.sendMessage(inputMessage)
                    inputMessage = ""
                },
                enabled = status == "Connected"
            ) {
                Text("Send")
            }
        }
    }
}
