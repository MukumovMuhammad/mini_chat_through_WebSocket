import android.util.Log
import com.example.mini_chat_test.DataClasses.EnumUserStatus
import com.example.mini_chat_test.DataClasses.MessageData
import com.example.mini_chat_test.DataClasses.UserDataResponse
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class AppWebSocketListener(
    private val onMessage: (String) -> Unit,
    private val onStatus: (EnumUserStatus) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        onStatus(EnumUserStatus.CONNECTED)
//        onMessage("--- Connection established ---")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.i("WebSocketTAG", "We are having some text messages here")
        Log.i("WebSocketTAG", "Message ${text}")
//        val json = Json { ignoreUnknownKeys = true }
//        val result = text.let { json.decodeFromString<MessageData>(it) }
        onMessage(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.i("WebSocketTAG", "We are having some bytes messages here")
        Log.i("WebSocketTAG", "Message ${bytes}")
        // Handle byte messages if needed
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.e("WEbSocketTAG", "The WebSocket is closed")
        onStatus(EnumUserStatus.DISCONNECTED)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WEbSocketTAG", "The WebSocket is Failed")
        onStatus(EnumUserStatus.ERROR)
        t.printStackTrace()
    }
}
