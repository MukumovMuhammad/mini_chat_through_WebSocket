package com.example.mini_chat_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun showOkDialog(title: String, messageText: String, onDismiss: () -> Unit) {
        Dialog(onDismissRequest = onDismiss) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(messageText)

                    Spacer(Modifier.height(16.dp))

                    Button(onClick = onDismiss) {
                        Text("Ok")
                    }
                }
            }
        }
}


@Composable
fun LoadingDialog(show: Boolean, message: String = "Loading...") {
    if (show) {
        Dialog(onDismissRequest = { /* prevent closing */ }) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
