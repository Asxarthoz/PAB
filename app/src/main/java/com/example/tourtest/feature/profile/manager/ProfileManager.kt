
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

    suspend fun loadUserFromFile() {
        _isLoading.value = true

        withContext(Dispatchers.IO) {
            val userId = AuthManager.getCurrentUserId()
            if (userId != null) {
                val user = AuthManager.getUserById(context, userId)
                _userState.update { user }
                if (user?.profileImage != null) {
                    _profileImagePath.value = user.profileImage
                }
            } else {
                _error.value = "User tidak ditemukan"
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }

            val correctPath = file.absolutePath
            println("Foto disimpan di: $correctPath")

            _profileImagePath.value = correctPath

            val currentUser = _userState.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(profileImage = correctPath)
                _userState.value = updatedUser
                AuthManager.updateUser(context, updatedUser)
                println("Path disimpan ke user: $correctPath")
            }

            correctPath
        } catch (e: Exception) {
            println("Error: ${e.message}")
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
                    println("File foto dihapus: $currentPath")
                }
            }

            val currentUser = _userState.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(profileImage = null)
                _userState.value = updatedUser
                AuthManager.updateUser(context, updatedUser)
            }

            _profileImagePath.value = null
            println("Foto profil dihapus dari user")
        } catch (e: Exception) {
            println("Error deleteProfileImage: ${e.message}")
            e.printStackTrace()
        }
    }
    fun loadProfileImage(imagePath: String?): Bitmap? {
        return try {
            if (imagePath != null) {
                val file = File(imagePath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(imagePath)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

    suspend fun updatePassword(oldPassword: String, newPassword: String, confirmPassword: String): Boolean {
        _isLoading.value = true
        _error.value = null

        val currentUser = _userState.value

        if (currentUser == null) {
            _error.value = "User tidak ditemukan"
            _isLoading.value = false
            return false
        }

        if (oldPassword.isBlank()) {
            _error.value = "Password lama tidak boleh kosong"
            _isLoading.value = false
            return false
        }

        if (newPassword.length < 6) {
            _error.value = "Password baru minimal 6 karakter"
            _isLoading.value = false
            return false
        }

        if (oldPassword == newPassword) {
            _error.value = "Password baru harus berbeda dengan password lama"
            _isLoading.value = false
            return false
        }

        if (newPassword != confirmPassword) {
            _error.value = "Konfirmasi password tidak cocok"
            _isLoading.value = false
            return false
        }

        if (currentUser.password != oldPassword) {
            _error.value = "Password lama salah"
            _isLoading.value = false
            return false
        }

        return try {
            val updatedUser = currentUser.copy(password = newPassword)
            val success = AuthManager.updateUser(context, updatedUser)

            if (success) {
                _userState.value = updatedUser
                true
            } else {
                _error.value = "Gagal mengganti password"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Gagal mengganti password"
            false
        } finally {
            _isLoading.value = false
        }
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