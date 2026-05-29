package com.example.tourtest.feature.auth.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.auth.manager.AuthManager
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
    private val userSession: UserSession
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
                AuthManager.setCurrentUser("GUEST")
                userSession.saveSession("GUEST")

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

        viewModelScope.launch(Dispatchers.IO){
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
        if (emailOrNickname.isBlank()) {
            errorMessage = "Email atau Nickname tidak boleh kosong"
            return
        }
        if (password.isBlank()) {
            errorMessage = "Password tidak boleh kosong"
            return
        }

        val success = AuthManager.loginUser(context, emailOrNickname.trim(), password.trim())
        if (success) {
            val user = AuthManager.getLoggedInUser(context, emailOrNickname.trim(), password.trim())
            user?.let {
                AuthManager.setCurrentUser(it.id)
                userSession.saveSession(it.id)
                _loginSuccess.send(Unit)
            } ?: run {
                errorMessage = "Gagal mengambil data user"
            }
        } else {
            errorMessage = "Email/Nickname atau password salah!"
        }
    }

    private suspend fun performRegister() {
        when {
            name.isBlank() -> errorMessage = "Nama lengkap tidak boleh kosong"
            nickname.isBlank() -> errorMessage = "Nickname tidak boleh kosong"
            emailOrNickname.isBlank() -> errorMessage = "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrNickname).matches() ->
                errorMessage = "Email tidak valid"

            password.length < 6 -> errorMessage = "Password minimal 6 karakter"
            password != confirmPassword -> errorMessage =
                "Konfirmasi password tidak cocok"

            else -> {
                val success = AuthManager.registerUser(
                    context = context,
                    name = name.trim(),
                    nickname = nickname.trim(),
                    email = emailOrNickname.trim(),
                    password = password.trim()
                )

                if (success) {
                    val loginSuccess = AuthManager.loginUser(
                        context = context,
                        inputEmailOrNickName = emailOrNickname,
                        inputPassword = password
                    )
                    if (loginSuccess) {
                        val user = AuthManager.getLoggedInUser(
                            context = context,
                            emailOrNickName = emailOrNickname,
                            password = password
                        )
                        user?.let {
                            AuthManager.setCurrentUser(it.id)
                            userSession.saveSession(it.id)
                        }
                        _loginSuccess.send(Unit)
                    } else {
                        errorMessage = "Registrasi berhasil, silakan login"
                        isLogin = true
                        password = ""
                        confirmPassword = ""
                        isLoading = false
                    }
                } else {
                    errorMessage =
                        "Registrasi gagal. Email atau Nickname sudah digunakan!"
                    isLoading = false
                }
            }
        }
    }
}