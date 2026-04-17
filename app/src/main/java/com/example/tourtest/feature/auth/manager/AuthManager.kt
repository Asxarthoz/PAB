package com.example.tourtest.feature.auth.manager

import android.content.Context
import com.example.tourtest.model.Users


object AuthManager {
    private const val FILE_NAME = "datauser.txt"

    public fun saveUserToFile(context: Context, name: String, nickname:String, email: String, pass: String) {
        val newUser = Users(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            nickName = nickname,
            email = email,
            password = pass,
            role = "wisatawan",
            isVerified = false
        )
        val data = "${newUser.id}|${newUser.name}|${newUser.nickName}|${newUser.email}|${newUser.password}|${newUser.role}|${newUser.isVerified}"
        try {
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use {
                it.write(data.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun loginUser(context: Context, inputEmailorNickName: String, inputPass: String): Boolean {
        return try {
            val file = context.getFileStreamPath(FILE_NAME)
            if(file.exists()) {
                val content = context.openFileInput(FILE_NAME).bufferedReader().use { it.readText() }
                val parts = content.split("|")
                if (parts.size >= 5) {
                    val storedNickname = parts[2]
                    val storedEmail = parts[3]
                    val storedPassword = parts[4]

                    val isUserMatch = (inputEmailorNickName == storedEmail || inputEmailorNickName == storedNickname)
                    val isPasswordMatch = (inputPass == storedPassword)

                    isUserMatch && isPasswordMatch
                } else {
                    false
                }
            }
            else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}