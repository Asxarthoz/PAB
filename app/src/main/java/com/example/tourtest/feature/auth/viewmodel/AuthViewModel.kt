package com.example.tourtest.feature.auth.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.core.network.NetworkApiManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSession: UserSession,
    private val networkApiManager: NetworkApiManager
) : ViewModel() {

    var isLogin by mutableStateOf(true)
    var name by mutableStateOf("")
    var nickname by mutableStateOf("")
    var emailOrNickname by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val _loginSuccess = Channel<Unit>(Channel.BUFFERED)
    val loginSucces = _loginSuccess.receiveAsFlow()

    fun toggleMode() {
        isLogin = !isLogin
        errorMessage = null
        password = ""
        confirmPassword = ""
        if (isLogin) {
            name = ""
            nickname = ""
        } else {
            emailOrNickname = ""
        }
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun handleGuestLogin() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                userSession.clearSession()
                userSession.saveSession("GUEST", "GUEST")
                _loginSuccess.send(Unit)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.localizedMessage
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    fun handleAuthAction() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isLogin) {
                    performLogin()
                } else {
                    performRegister()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    private suspend fun performLogin() {
        val identity = emailOrNickname.trim()
        val pwd = password.trim()

        println("🔐 LOGIN: identity=$identity")

        val response = networkApiManager.login(identity, pwd)

        println("🔐 RESPONSE: $response")

        if (response != null) {
            println("✅ TOKEN: ${response.accessToken}")
            // Simpan token dulu
            userSession.saveToken(response.accessToken)

            // Ambil data user dari /me untuk mendapatkan ID numerik yang benar
            val userResponse = networkApiManager.getCurrentUser(response.accessToken)
            val userId = userResponse?.id?.toString() ?: response.username

            println("✅ USER ID: $userId, USERNAME: ${response.username}")
            userSession.saveSession(userId, response.username)
            _loginSuccess.send(Unit)
        } else {
            println("❌ LOGIN GAGAL")
            errorMessage = "Login gagal. Periksa email/username dan password Anda!"
        }
    }

    private suspend fun performRegister() {
        when {
            name.isBlank() -> errorMessage = "Nama lengkap tidak boleh kosong"
            nickname.isBlank() -> errorMessage = "Username tidak boleh kosong"
            emailOrNickname.isBlank() -> errorMessage = "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrNickname).matches() ->
                errorMessage = "Email tidak valid"
            password.length < 6 -> errorMessage = "Password minimal 6 karakter"
            password != confirmPassword -> errorMessage = "Konfirmasi password tidak cocok"
            else -> {
                // ✅ REGISTER VIA API
                val response = networkApiManager.register(
                    fullname = name.trim(),
                    username = nickname.trim(),
                    email = emailOrNickname.trim(),
                    password = password.trim(),
                    role = "tourist"
                )

                if (response != null) {
                    // Registrasi berhasil, langsung login
                    val loginResponse = networkApiManager.login(emailOrNickname.trim(), password.trim())
                    if (loginResponse != null) {
                        userSession.saveToken(loginResponse.accessToken)
                        // Ambil ID numerik dari /me
                        val userResponse = networkApiManager.getCurrentUser(loginResponse.accessToken)
                        val userId = userResponse?.id?.toString() ?: loginResponse.username
                        userSession.saveSession(userId, loginResponse.username)
                        _loginSuccess.send(Unit)
                    } else {
                        errorMessage = "Registrasi berhasil, silakan login"
                        isLogin = true
                        password = ""
                        confirmPassword = ""
                        isLoading = false
                    }
                } else {
                    errorMessage = "Registrasi gagal. Email atau Username sudah digunakan!"
                    isLoading = false
                }
            }
        }
    }
}