
package com.example.tourtest.feature.profile.manager

import android.content.Context
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.model.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PasswordManager(
    private val context: Context
) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentUser = MutableStateFlow<Users?>(null)
    val currentUser: StateFlow<Users?> = _currentUser.asStateFlow()

    suspend fun loadCurrentUser() {
        val userId = AuthManager.getCurrentUserId()
        if (userId != null) {
            val user = AuthManager.getUserById(context, userId)
            _currentUser.value = user
        }
    }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        _isLoading.value = true
        _error.value = null
        _isSuccess.value = false

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

        val currentUser = _currentUser.value
        if (currentUser == null) {
            _error.value = "User tidak ditemukan"
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
                _currentUser.value = updatedUser
                _isSuccess.value = true
                true
            } else {
                _error.value = "Gagal mengganti password"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Terjadi kesalahan"
            false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetSuccess() {
        _isSuccess.value = false
    }

    fun resetState() {
        _isLoading.value = false
        _isSuccess.value = false
        _error.value = null
    }
}