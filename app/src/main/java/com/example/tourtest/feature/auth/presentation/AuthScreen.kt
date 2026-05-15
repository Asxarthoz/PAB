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
import com.example.tourtest.ui.theme.TourizmeTheme
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.tourtest.feature.auth.viewmodel.AuthViewModel

sealed class AuthFormState{
    data class Name(val value: String) : AuthFormState()
    data class Nickname(val value: String) : AuthFormState()
    data class Email(val value: String) : AuthFormState()
    data class Password(val value: String) : AuthFormState()
    data class ConfirmPassword(val value: String) : AuthFormState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthContent(
    isLogin: Boolean,
    name: String,
    nickname: String,
    emailOrNickname: String,
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onStateChange: (AuthFormState) -> Unit,
    onToggleMode: () -> Unit,
    onAuthClick: () -> Unit,
    onTogglePasswordVisibility: () -> Unit
){
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
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
                        text = errorMessage,
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
                        onValueChange = { onStateChange(AuthFormState.Name(it)) },
                        label = { Text("Nama lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = errorMessage?.contains("Nama") == true
                    )

                    TextField(
                        value = nickname,
                        onValueChange = { onStateChange(AuthFormState.Nickname(it)) },
                        label = { Text("Nickname") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = errorMessage?.contains("Nickname") == true
                    )
                }

                TextField(
                    value = emailOrNickname,
                    onValueChange = { onStateChange(AuthFormState.Email(it)) },
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
                    onValueChange = { onStateChange(AuthFormState.Password(it)) },
                    label = { Text("Kata sandi") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick =  onTogglePasswordVisibility ) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    isError = errorMessage?.contains("password") == true
                )

                if (!isLogin) {
                    TextField(
                        value = confirmPassword,
                        onValueChange = { onStateChange(AuthFormState.ConfirmPassword(it)) },
                        label = { Text("Konfirmasi kata sandi") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick =  onTogglePasswordVisibility ) {
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
                        onClick = onAuthClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLogin) "Masuk" else "Daftar")
                    }
                }

                TextButton(
                    onClick = onToggleMode,
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loginSucces.collect {
            onLoginSuccess()
        }
    }

    AuthContent(
        isLogin = viewModel.isLogin,
        name = viewModel.name,
        nickname = viewModel.nickname,
        emailOrNickname = viewModel.emailOrNickname,
        password = viewModel.password,
        confirmPassword = viewModel.confirmPassword,
        passwordVisible = viewModel.passwordVisible,
        isLoading = viewModel.isLoading,
        errorMessage = viewModel.errorMessage,
        onStateChange = { state ->
            when (state) {
                is AuthFormState.Name -> viewModel.name = state.value
                is AuthFormState.Nickname -> viewModel.nickname = state.value
                is AuthFormState.Email -> viewModel.emailOrNickname = state.value
                is AuthFormState.Password -> viewModel.password = state.value
                is AuthFormState.ConfirmPassword -> viewModel.confirmPassword = state.value
            }
        },
        onToggleMode = { viewModel.toggleMode() },
        onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
        onAuthClick = { viewModel.handleAuthAction() }
    )
}

@Preview(showSystemUi = true)
@Composable
fun LoginPreview() {
    TourizmeTheme {
        AuthContent(
            isLogin = true,
            name = "",
            nickname = "",
            emailOrNickname = "tian@mail.com",
            password = "password",
            confirmPassword = "",
            passwordVisible = false,
            isLoading = false,
            errorMessage = null,
            onStateChange = {},
            onToggleMode = {},
            onAuthClick = {},
            onTogglePasswordVisibility = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterPreview() {
    TourizmeTheme {
        AuthContent(
            isLogin = false,
            name = "Tian Qoqo",
            nickname = "Qoqo",
            emailOrNickname = "tian@mail.com",
            password = "password",
            confirmPassword = "password",
            passwordVisible = true,
            isLoading = false,
            errorMessage = "Contoh Pesan Error",
            onStateChange = {},
            onToggleMode = {},
            onAuthClick = {},
            onTogglePasswordVisibility = {}
        )
    }
}