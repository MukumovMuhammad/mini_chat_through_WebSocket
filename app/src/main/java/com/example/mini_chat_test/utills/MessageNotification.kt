package com.example.mini_chat_test.utills

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
// Remove the unused import: import android.provider.Settings.Global.getString
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat // Keep this import for the KTX approach
import com.example.mini_chat_test.R

val CHANNEL_ID = "My_CHANNEL_ID"

// FIX 1: Pass Context into the function
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "message" // Define in strings.xml
        val descriptionText = "Message notification" // Define in strings.xml
        val importance = NotificationManager.IMPORTANCE_DEFAULT // Or HIGH, LOW, MIN
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        // FIX 2 (Option A: Using KTX extension for clarity and safety):
        // Ensure you have "androidx.core:core-ktx:1.12.0" (or newer) in your build.gradle
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)

        // FIX 2 (Option B: Manual casting, less preferred):
        // val notificationManager: NotificationManager =
        //     context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }
}

fun showNotification(context: Context, title: String, message: String) {
    // Call the channel creation function here before showing the notification
    createNotificationChannel(context)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.mini_chat_icon)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Or HIGH, LOW, MIN

    // To make the notification expandable for long text:
    // .setStyle(NotificationCompat.BigTextStyle().bigText(longContent))

    val notificationManager = NotificationManagerCompat.from(context)
    // notificationId should be unique for each notification to prevent overriding
    val notificationId = System.currentTimeMillis().toInt()
    notificationManager.notify(notificationId, builder.build())
}
