package com.example.tourtest.feature.auth.presentation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator // Gunakan ini jika ContainedLoadingIndicator bermasalah
//import androidx.compose.material3.LoadingIndicator // Untuk Material 3 terbaru
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tourtest.ui.theme.TourizmeTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    Content(
        isLogin = isLogin,
        name = name,
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        onTogglePasswordVisibility = { passwordVisible = !passwordVisible },

        onChangeName = { name = it },
        onChangeEmail = { email = it },
        onChangePassword = { password = it },

        onClickSubmit = {
            if(!isLogin) {
                if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    saveUserToFile(context, name, email, password)
                    onLoginSuccess()
                }
            } else {
                if(loginUser(context, email, password)) {
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
    email: String = "",
    password: String = "",
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = { },
    confirmPassword: String = "",
    onChangeName: (String) -> Unit = {},
    onChangeEmail: (String) -> Unit = {},
    onChangePassword: (String) -> Unit = {},
    onChangeConfirmPassword: (String) -> Unit = {},
    onClickSwitch: () -> Unit = {},
    onClickSubmit: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Masuk")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
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
                }
                TextField(
                    value = email,
                    onValueChange = onChangeEmail,
                    label = { Text("Alamat email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
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

private fun saveUserToFile(context: Context, name: String, email: String, pass: String) {
    val data = "$name|$email|$pass"
    try {
        context.openFileOutput("datauser.txt", Context.MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun loginUser(context: Context, inputEmail: String, inputPass: String): Boolean {
    return try {
        val file = context.getFileStreamPath("datauser.txt")
        if(file.exists()) {
            val content = context.openFileInput("datauser.txt").bufferedReader().use { it.readText() }
            val parts = content.split("|")
            parts[1] == inputEmail && parts[2] == inputPass
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}