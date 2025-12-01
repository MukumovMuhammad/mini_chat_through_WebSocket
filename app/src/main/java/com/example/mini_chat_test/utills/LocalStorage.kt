package com.example.mini_chat_test.utills

import android.content.Context
import android.util.Log

fun saveToSharedPreferences(context: Context, key: String, value: String) {
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString(key, value)
        apply()
    }
}

fun saveUsernameAndId(context: Context, username: String, id: Int?) {
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("id", id.toString())
        putString("username", username)
        apply()
    }
}


fun getSavedId(context: Context): Int? {
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    var returnValue =  sharedPref.getString("id", null)
    if (returnValue == "null"){
        Log.e("SavedID_TAG", "the id is nul or empty")
        return null
    }
    else{
        Log.e("SavedID_TAG", "the id is fine and can be returned. The id is ${returnValue}")
        return returnValue?.toInt()
    }
    
}
fun getSavedUsername(context: Context): String? {
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val returnValue = sharedPref.getString("username", null)
    if (returnValue == "null"){
        Log.e("SavedUsername_TAG", "the username is nul or empty")
        return null
    }
    else{
        Log.e("SavedUsername_TAG", "the username is fine and can be returned. The name is ${returnValue}")
        return returnValue
    }
}
