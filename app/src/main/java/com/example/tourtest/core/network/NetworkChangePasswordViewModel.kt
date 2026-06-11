package com.example.tourtest.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.core.network.NetworkApiManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkChangePasswordViewModel @Inject constructor(
    private val networkApiManager: NetworkApiManager,
    private val userSession: UserSession
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _error.value = "Semua kolom wajib diisi!"
            return
        }

        if (newPassword != confirmPassword) {
            _error.value = "Konfirmasi password baru tidak cocok!"
            return
        }

        if (newPassword.length < 6) {
            _error.value = "Password baru minimal harus 6 karakter!"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Ambil token dari session (menggunakan .first() jika properti token berbentuk Flow)
                val token = userSession.token.first()

                if (!token.isNullOrEmpty()) {
                    val success = networkApiManager.updateProfile(
                        token = token,
                        password = newPassword
                    )

                    if (success) {
                        _isSuccess.value = true
                    } else {
                        _error.value = "Gagal mengubah password. Silakan coba lagi."
                    }
                } else {
                    _error.value = "Sesi habis, silakan login kembali."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Terjadi kesalahan jaringan: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetState() {
        _isLoading.value = false
        _isSuccess.value = false
        _error.value = null
    }
}