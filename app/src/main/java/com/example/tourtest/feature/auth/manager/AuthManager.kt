// feature/auth/manager/AuthManager.kt
package com.example.tourtest.feature.auth.manager

import android.content.Context
import com.example.tourtest.model.Users
import java.io.File

object AuthManager {
    private const val INTERNAL_FILE_NAME = "datauser.txt"
    private const val ASSETS_FILE_NAME = "datauser.txt"

    /**
     * Inisialisasi: Copy data dari assets ke internal storage (hanya sekali)
     */
    fun initializeDataFromAssets(context: Context): Boolean {
        return try {
            val internalFile = File(context.filesDir, INTERNAL_FILE_NAME)
            if (!internalFile.exists()) {
                val inputStream = context.assets.open(ASSETS_FILE_NAME)
                val outputStream = context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_PRIVATE)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Mendapatkan semua user dari internal storage
     */
    fun getAllUsers(context: Context): List<Users> {
        val users = mutableListOf<Users>()
        return try {
            val file = context.getFileStreamPath(INTERNAL_FILE_NAME)
            if (file.exists()) {
                val content = context.openFileInput(INTERNAL_FILE_NAME).bufferedReader().use { it.readText() }
                val lines = content.lines()
                for (line in lines) {
                    if (line.isNotBlank()) {
                        val parts = line.split("|")
                        if (parts.size >= 7) {
                            users.add(
                                Users(
                                    id = parts[0],
                                    name = parts[1],
                                    nickName = parts[2],
                                    email = parts[3],
                                    password = parts[4],
                                    role = parts[5],
                                    isVerified = parts[6].toBoolean(),
                                    profileImage = if (parts.size > 7) parts[7] else null
                                )
                            )
                        }
                    }
                }
            }
            users
        } catch (e: Exception) {
            e.printStackTrace()
            users
        }
    }

    /**
     * Register user baru
     */
    fun registerUser(
        context: Context,
        name: String,
        nickname: String,
        email: String,
        password: String
    ): Boolean {
        return try {
            if (isUserExists(context, email, nickname)) {
                return false
            }

            val newUser = Users(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                nickName = nickname,
                email = email,
                password = password,
                role = "wisatawan",
                isVerified = false,
                profileImage = null
            )

            val data = "${newUser.id}|${newUser.name}|${newUser.nickName}|${newUser.email}|${newUser.password}|${newUser.role}|${newUser.isVerified}|\n"

            context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_APPEND).use { outputStream ->
                outputStream.write(data.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Login user
     */
    fun loginUser(
        context: Context,
        inputEmailOrNickName: String,
        inputPassword: String
    ): Boolean {
        val users = getAllUsers(context)
        return users.any { user ->
            (user.email == inputEmailOrNickName || user.nickName == inputEmailOrNickName) &&
                    user.password == inputPassword
        }
    }

    /**
     * Mendapatkan data user yang login
     */
    fun getLoggedInUser(
        context: Context,
        emailOrNickName: String,
        password: String
    ): Users? {
        val users = getAllUsers(context)
        return users.find { user ->
            (user.email == emailOrNickName || user.nickName == emailOrNickName) &&
                    user.password == password
        }
    }

    /**
     * Mendapatkan user berdasarkan ID
     */
    fun getUserById(context: Context, userId: String): Users? {
        val users = getAllUsers(context)
        return users.find { it.id == userId }
    }

    /**
     * Cek apakah user sudah ada
     */
    fun isUserExists(context: Context, email: String, nickname: String): Boolean {
        val users = getAllUsers(context)
        return users.any { it.email == email || it.nickName == nickname }
    }

    /**
     * Update data user di file
     */
    fun updateUser(context: Context, updatedUser: Users): Boolean {
        return try {
            val users = getAllUsers(context).toMutableList()
            val index = users.indexOfFirst { it.id == updatedUser.id }
            if (index != -1) {
                users[index] = updatedUser
                saveAllUsers(context, users)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Update password user
     */


    /**
     * Menyimpan semua user ke file
     */
    private fun saveAllUsers(context: Context, users: List<Users>): Boolean {
        return try {
            val stringBuilder = StringBuilder()
            for (user in users) {
                // Format: id|name|nickName|email|password|role|isVerified|profileImage
                stringBuilder.append("${user.id}|${user.name}|${user.nickName}|${user.email}|${user.password}|${user.role}|${user.isVerified}|${user.profileImage ?: ""}\n")
            }
            context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(stringBuilder.toString().toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    // ============================================
    // SESSION MANAGEMENT
    // ============================================

    private var currentUserId: String? = null

    fun setCurrentUser(userId: String) {
        currentUserId = userId
    }

    fun getCurrentUserId(): String? {
        return currentUserId
    }

    fun getCurrentUser(context: Context): Users? {
        val userId = currentUserId ?: return null
        return getUserById(context, userId)
    }

    fun clearCurrentUser() {
        currentUserId = null
    }

    fun isLoggedIn(): Boolean {
        return currentUserId != null
    }

    fun logout(): Boolean {
        clearCurrentUser()
        return true
    }
}