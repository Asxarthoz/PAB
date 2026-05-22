package com.example.tourtest.feature.profile.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.model.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ProfileManager(
    private val context: Context
) {
    private val _userState = MutableStateFlow<Users?>(null)
    val userState: StateFlow<Users?> = _userState.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()
    private val _profileImagePath = MutableStateFlow<String?>(null)
    val profileImagePath: StateFlow<String?> = _profileImagePath.asStateFlow()
    private val imageCache = mutableMapOf<String, Bitmap>()

    suspend fun loadUserFromFile(userId: String) {
        _isLoading.value = true
        _error.value = null

        withContext(Dispatchers.IO) {
            try {
                val user = AuthManager.getUserById(context, userId)
                if (user != null) {
                    _userState.update { user }
                    if (user?.profileImage != null) {
                        _profileImagePath.value = user.profileImage
                    }
                } else {
                    _error.value = "User tidak ditemukan"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }

        _isLoading.value = false
    }

    suspend fun updateProfile(
        name: String,
        nickName: String,
        email: String
    ): Boolean {
        _isLoading.value = true
        _error.value = null
        _updateSuccess.value = false

        if (name.isBlank()) {
            _error.value = "Nama tidak boleh kosong"
            _isLoading.value = false
            return false
        }

        if (nickName.isBlank()) {
            _error.value = "Nama panggilan tidak boleh kosong"
            _isLoading.value = false
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _error.value = "Email tidak valid"
            _isLoading.value = false
            return false
        }

        val currentUser = _userState.value

        if (currentUser == null) {
            _error.value = "User tidak ditemukan"
            _isLoading.value = false
            return false
        }

        return try {
            val updatedUser = currentUser.copy(
                name = name,
                nickName = nickName,
                email = email
            )

            val success = AuthManager.updateUser(context, updatedUser)

            if (success) {
                _userState.value = updatedUser
                _updateSuccess.value = true
                true
            } else {
                _error.value = "Gagal menyimpan perubahan"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Gagal update profil"
            false
        } finally {
            _isLoading.value = false
        }
    }

    fun saveProfileImage(bitmap: Bitmap): String? {
        return try {
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val directory = context.filesDir.resolve("profile_images")

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            }

            val correctPath = file.absolutePath
            _profileImagePath.value = correctPath

            val currentUser = _userState.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(profileImage = correctPath)
                _userState.value = updatedUser
                AuthManager.updateUser(context, updatedUser)
            }

            imageCache[correctPath] = bitmap

            correctPath
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun loadProfileImage(imagePath: String?): Bitmap? {
        return try {
            if (imagePath == null) return null

            imageCache[imagePath]?.let { return it }

            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                bitmap?.let { imageCache[imagePath] = it }
                bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteProfileImage() {
        try {
            val currentPath = _profileImagePath.value
            if (currentPath != null) {
                val file = File(currentPath)
                if (file.exists()) {
                    file.delete()
                    imageCache.remove(currentPath)
                }
            }

            val currentUser = _userState.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(profileImage = null)
                _userState.value = updatedUser
                AuthManager.updateUser(context, updatedUser)
            }

            _profileImagePath.value = null
        } catch (e: Exception) {
            println("Error deleteProfileImage: ${e.message}")
            e.printStackTrace()
        }
    }

    fun loadProfileImageFromUser(): Bitmap? {
        val currentUser = _userState.value
        val path = currentUser?.profileImage
        return if (path != null) {
            loadProfileImage(path)
        } else {
            null
        }
    }

    fun updateProfileImagePath(path: String?) {
        _profileImagePath.value = path
    }

    fun loadUser(user: Users) {
        _userState.update { user }
    }

    fun getCurrentUser(): Users? {
        return _userState.value
    }

    fun clearError() {
        _error.value = null
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }
}