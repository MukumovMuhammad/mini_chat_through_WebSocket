package com.example.mini_chat_test.Activities

import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mini_chat_test.R
import com.example.mini_chat_test.ui.theme.Mini_chat_testTheme
import com.example.mini_chat_test.utills.LoadingDialog
import com.example.mini_chat_test.websocket.ChatViewModel
import kotlinx.coroutines.delay
import kotlin.getValue

private var TAG = "MAinActivity_TAG"


class RegistrationActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    var Spleash_animation_finished = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mini_chat_testTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if (Spleash_animation_finished.value){
                        LoginScreen(viewModel, modifier = Modifier.padding(innerPadding))
                    }
                    else{
                        SplashScreen(){ finished ->
                            Spleash_animation_finished.value = finished
                        }
                    }



                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: ChatViewModel, modifier: Modifier) {


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

@Composable
fun SplashScreen(AnimationFinished: (Boolean) -> Unit) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)

        AnimationFinished(true)
    }

    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.drawable.mini_chat_logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    var viewModel: ChatViewModel = ChatViewModel()

    Mini_chat_testTheme {
//        SplashScreen(){
//
//        }
        LoginScreen(viewModel = viewModel, modifier = Modifier)
    }
}