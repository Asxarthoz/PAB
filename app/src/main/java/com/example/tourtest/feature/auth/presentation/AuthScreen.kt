package com.example.tourtest.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.tourtest.feature.auth.viewmodel.AuthViewModel
import com.example.tourtest.ui.theme.InterFontFamily
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBgDark
import com.example.tourtest.ui.theme.TourizmeBgDarkTop
import com.example.tourtest.ui.theme.TourizmeBlueDark
import com.example.tourtest.ui.theme.TourizmeBlueMain
import com.example.tourtest.ui.theme.TourizmeHighlightGreen
import com.example.tourtest.ui.theme.TourizmeSurfaceLight
import com.example.tourtest.ui.theme.TourizmeTextPrimary
import com.example.tourtest.ui.theme.TourizmeTextSecondary

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
    onTogglePasswordVisibility: () -> Unit,
    onGuestLogin: () -> Unit
){
    val isDark = isSystemInDarkTheme()
    val backgroundGradient = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(TourizmeBgDarkTop, TourizmeBgDark)
        } else {
            listOf(TourizmeBlueMain, TourizmeBlueDark)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLogin) {
                Spacer(modifier = Modifier.height(120.dp))

                Text(
                    text = "TOURIZME",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontSize = 26.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(64.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Selamat Datang",
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        fontFamily = InterFontFamily
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Silakan masuk untuk melanjutkan\npetualangan Anda.",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = InterFontFamily
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "TOURIZME",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 26.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Banner Pesan Error Komponen (Jika validasi gagal)
            if (errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // 💡 KONDISI REGISTER: JUDUL MASUK KE DALAM CARD (Sesuai Figma Register)
                    if (!isLogin) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Buat Akun Baru",
                                fontFamily = MontserratFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = if (isDark) Color.White else TourizmeBlueMain
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mulai petualangan Anda menjelajahi destinasi wisata terbaik.",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = if (isDark) Color.LightGray else TourizmeTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (!isLogin) {
                        FormLabelAndTextField(
                            label = "Nama Lengkap",
                            value = name,
                            onValueChange = { onStateChange(AuthFormState.Name(it)) },
                            placeholder = "Masukkan nama lengkap",
                            enabled = !isLoading,
                            isDark = isDark
                        )
                        FormLabelAndTextField(
                            label = "Username",
                            value = nickname,
                            onValueChange = { onStateChange(AuthFormState.Nickname(it)) },
                            placeholder = "Masukkan Username",
                            enabled = !isLoading,
                            isDark = isDark
                        )
                    }

                    FormLabelAndTextField(
                        label = if (isLogin) "Username or Email" else "Email",
                        value = emailOrNickname,
                        onValueChange = { onStateChange(AuthFormState.Email(it)) },
                        placeholder = if (isLogin) "nama@email.com" else "example@mail.com",
                        enabled = !isLoading,
                        isDark = isDark,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kata Sandi",
                                color = if (isDark) TourizmeHighlightGreen else TourizmeTextPrimary,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            if (isLogin) {
                                TextButton(
                                    onClick = { },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text(
                                        text = "Lupa Kata Sandi?",
                                        color = if (isDark) TourizmeHighlightGreen else TourizmeBlueMain,
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { onStateChange(AuthFormState.Password(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            singleLine = true,
                            placeholder = {
                                Text(
                                    if (isLogin) "••••••••" else "Min. 8 karakter",
                                    color = Color.LightGray
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image =
                                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = onTogglePasswordVisibility) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = if (isDark) TourizmeBgDarkTop else Color.White,
                                unfocusedContainerColor = if (isDark) TourizmeBgDarkTop else Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            textStyle = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = TourizmeTextPrimary
                            )
                        )
                    }

                    if (!isLogin) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Konfirmasi Kata Sandi",
                                color = if (isDark) TourizmeHighlightGreen else TourizmeTextPrimary,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { onStateChange(AuthFormState.ConfirmPassword(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                singleLine = true,
                                placeholder = {
                                    Text(
                                        "Ulangi kata sandi",
                                        color = Color.LightGray
                                    )
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    val image =
                                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                    IconButton(onClick = onTogglePasswordVisibility) {
                                        Icon(
                                            imageVector = image,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = if (isDark) TourizmeBgDarkTop else Color.White,
                                    unfocusedContainerColor = if (isDark) TourizmeBgDarkTop else Color.White,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                textStyle = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    color = TourizmeTextPrimary
                                )
                            )
                        }

                        if (password.isNotEmpty() && password.length < 8) {
                            Text(
                                text = "Password minimal 8 karakter",
                                color = MaterialTheme.colorScheme.error,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = TourizmeBlueMain
                        )
                    } else {
                        Button(
                            onClick = onAuthClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) TourizmeBlueMain else Color(0xFF006194)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isLogin) "Masuk" else "Daftar Sekarang",
                                    color = if (isDark) TourizmeHighlightGreen else Color.White,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLogin) "Belum punya akun? " else "Sudah punya akun? ",
                            color = if (isDark) TourizmeSurfaceLight else TourizmeTextPrimary,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        TextButton(
                            onClick = onToggleMode,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(
                                text = if (isLogin) "Daftar Sekarang" else "Masuk di sini",
                                color = if (isLogin) TourizmeHighlightGreen else TourizmeBlueMain,
                                fontWeight = FontWeight.Bold,
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            if (isLogin && !isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onGuestLogin) {
                    Text(
                        text = "Masuk Sebagai Tamu",
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FormLabelAndTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    isDark: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = if (isDark) TourizmeHighlightGreen else TourizmeTextPrimary,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) TourizmeBgDarkTop else Color.White,
                unfocusedContainerColor = if (isDark) TourizmeBgDarkTop else Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = TourizmeTextPrimary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onGuestLogin: () -> Unit
) {
    val isLogin = viewModel.isLogin
    val name = viewModel.name
    val nickname = viewModel.nickname
    val emailOrNickname = viewModel.emailOrNickname
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val passwordVisible = viewModel.passwordVisible
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(viewModel.loginSucces) {
        viewModel.loginSucces.collect {
            onLoginSuccess()
        }
    }

    AuthContent(
        isLogin = isLogin,
        name = name,
        nickname = nickname,
        emailOrNickname = emailOrNickname,
        password = password,
        confirmPassword = confirmPassword,
        passwordVisible = passwordVisible,
        isLoading = isLoading,
        errorMessage = errorMessage,
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
        onAuthClick = { viewModel.handleAuthAction() },
        onGuestLogin = onGuestLogin
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
            onTogglePasswordVisibility = {},
            onGuestLogin = {}
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
            onTogglePasswordVisibility = {},
            onGuestLogin = {}
        )
    }
}