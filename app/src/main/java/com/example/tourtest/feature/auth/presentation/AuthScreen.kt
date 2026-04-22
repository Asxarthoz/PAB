
package com.example.tourtest.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.ui.theme.TourizmeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var emailOrNickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        AuthManager.initializeDataFromAssets(context)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Tourizme",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = if (isLogin) "Masuk ke Akun Anda" else "Buat akun baru",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )

            if (errorMessage != null) {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = errorMessage!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isLogin) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = errorMessage?.contains("Nama") == true
                    )

                    TextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Nickname") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = errorMessage?.contains("Nickname") == true
                    )
                }

                TextField(
                    value = emailOrNickname,
                    onValueChange = { emailOrNickname = it },
                    label = {
                        Text(
                            if (isLogin) "Email atau Nickname"
                            else "Alamat Email"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = errorMessage?.contains("Email") == true ||
                            errorMessage?.contains("Nickname") == true
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata sandi") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    isError = errorMessage?.contains("password") == true
                )

                if (!isLogin) {
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi kata sandi") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        isError = errorMessage?.contains("Konfirmasi") == true ||
                                (confirmPassword.isNotEmpty() && password != confirmPassword)
                    )

                    if (password.isNotEmpty() && password.length < 6) {
                        Text(
                            text = "Password minimal 6 karakter",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                        Text(
                            text = "Password tidak cocok",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null

                            if (isLogin) {
                                if (emailOrNickname.isBlank()) {
                                    errorMessage = "Email atau Nickname tidak boleh kosong"
                                    isLoading = false
                                    return@Button
                                }

                                if (password.isBlank()) {
                                    errorMessage = "Password tidak boleh kosong"
                                    isLoading = false
                                    return@Button
                                }

                                val success = AuthManager.loginUser(
                                    context = context,
                                    inputEmailOrNickName = emailOrNickname,
                                    inputPassword = password
                                )

                                if (success) {
                                    val user = AuthManager.getLoggedInUser(
                                        context = context,
                                        emailOrNickName = emailOrNickname,
                                        password = password
                                    )
                                    user?.let {
                                        AuthManager.setCurrentUser(it.id)
                                    }
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Email/Nickname atau password salah!"
                                }
                                isLoading = false
                            } else {
                                when {
                                    name.isBlank() -> errorMessage = "Nama lengkap tidak boleh kosong"
                                    nickname.isBlank() -> errorMessage = "Nickname tidak boleh kosong"
                                    emailOrNickname.isBlank() -> errorMessage = "Email tidak boleh kosong"
                                    !android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrNickname).matches() ->
                                        errorMessage = "Email tidak valid"
                                    password.length < 6 -> errorMessage = "Password minimal 6 karakter"
                                    password != confirmPassword -> errorMessage = "Konfirmasi password tidak cocok"
                                    else -> {
                                        val success = AuthManager.registerUser(
                                            context = context,
                                            name = name,
                                            nickname = nickname,
                                            email = emailOrNickname,
                                            password = password
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
                                                }
                                                onLoginSuccess()
                                            } else {
                                                errorMessage = "Registrasi berhasil, silakan login"
                                                isLogin = true
                                                password = ""
                                                confirmPassword = ""
                                            }
                                        } else {
                                            errorMessage = "Registrasi gagal. Email atau Nickname sudah digunakan!"
                                        }
                                    }
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLogin) "Masuk" else "Daftar")
                    }
                }

                TextButton(
                    onClick = {
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
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLogin) "Belum punya akun? Daftarkan akun baru"
                        else "Sudah punya akun? Masuk sekarang"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
private fun LoginPreview() {
    TourizmeTheme {
        AuthScreen(onLoginSuccess = {})
    }
}

@Preview(showBackground = true, name = "Register Screen")
@Composable
private fun RegisterPreview() {
    TourizmeTheme {
        AuthScreen(onLoginSuccess = {})
    }
}