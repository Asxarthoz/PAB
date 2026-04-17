package com.example.tourtest.feature.auth.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tourtest.ui.theme.TourizmeTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.tourtest.model.Users
import com.example.tourtest.feature.auth.manager.AuthManager


@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Content(
        isLogin = isLogin,
        name = name,
        nickname = nickname,
        email = email,
        password = password,
        confirmPassword = password,
        passwordVisible = passwordVisible,
        onTogglePasswordVisibility = { passwordVisible = !passwordVisible },

        onChangeName = { name = it },
        onChangeNickName = { nickname = it },
        onChangeEmail = { email = it },
        onChangePassword = { password = it },

        onClickSubmit = {
            if(!isLogin) {
                if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    AuthManager.saveUserToFile(context, name, nickname, email, password)
                    onLoginSuccess()
                }
            } else {
                if(AuthManager.loginUser(context, email, password)) {
                    onLoginSuccess()
                }
                else if(AuthManager.loginUser(context, nickname, password)) {
                    onLoginSuccess()
                } else {

                }
            }
        },
        onClickSwitch = {
            isLogin = !isLogin
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    isLogin: Boolean = true,
    isLoading: Boolean = false,
    name: String = "",
    nickname: String = "",
    email: String = "",
    password: String = "",
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = { },
    confirmPassword: String = "",
    onChangeName: (String) -> Unit = {},
    onChangeNickName: (String)  -> Unit = {},
    onChangeEmail: (String) -> Unit = {},
    onChangePassword: (String) -> Unit = {},
    onChangeConfirmPassword: (String) -> Unit = {},
    onClickSwitch: () -> Unit = {},
    onClickSubmit: () -> Unit = {}
) {
    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
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
                modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isLogin) {
                    TextField(
                        value = name,
                        onValueChange = onChangeName,
                        label = { Text("Nama lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    TextField(
                        value = nickname,
                        onValueChange = onChangeNickName,
                        label = { Text("Nickname") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )
                }
                TextField(
                    value = email,
                    onValueChange = onChangeEmail,
                    label = { Text("Alamat email atau Nickname") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                TextField(
                    value = password,
                    onValueChange = onChangePassword,
                    label = { Text("Kata sandi") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                        IconButton(onClick = onTogglePasswordVisibility) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    }
                )
                if (!isLogin) {
                    TextField(
                        value = confirmPassword,
                        onValueChange = onChangeConfirmPassword,
                        label = { Text("Konfirmasi kata sandi") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                            IconButton(onClick = onTogglePasswordVisibility) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )
                }
            }

            // Button Group
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
                        onClick = onClickSubmit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLogin) "Masuk" else "Daftar")
                    }
                }

                TextButton(
                    onClick = onClickSwitch,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLogin) "Belum punya akun? Daftarkan akun baru"
                        else "Sudah punya akun? Masuk sekarang"
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true, group = "Login")
@Composable
private fun LoginPreview() {
    TourizmeTheme {
        Content(isLogin = true)
    }
}

@Preview(showBackground = true, group = "Register")
@Composable
private fun RegisterPreview() {
    TourizmeTheme {
        Content(isLogin = false)
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun LoadingPreview() {
    TourizmeTheme {
        Content(isLoading = true)
    }
}
