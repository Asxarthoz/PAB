package com.example.tourtest.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tourtest.feature.profile.viewmodel.ChangePasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordContent(
    oldPassword: String,
    newPassword: String,
    confirmPassword: String,
    showOldPassword: Boolean,
    showNewPassword: Boolean,
    showConfirmPassword: Boolean,
    isLoading: Boolean,
    error: String?,
    onOldPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleOldPassword: () -> Unit,
    onToggleNewPassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    onClearError: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
){
    val isSameAsOld = newPassword.isNotEmpty() && oldPassword.isNotEmpty() && newPassword == oldPassword
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ganti Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }, actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = !isLoading &&
                                oldPassword.isNotBlank() &&
                                newPassword.isNotBlank() &&
                                newPassword == confirmPassword &&
                                newPassword.length >= 6 &&
                                !isSameAsOld
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Simpan"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Simpan")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)), // Selaras dengan Profile & Edit Profile
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ubah Kata Sandi Anda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Pastikan password baru aman dan mudah diingat",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Form Perubahan Sandi",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = error, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = onClearError) {
                            Icon(Icons.Default.Check, contentDescription = "Tutup", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }


            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = onOldPasswordChange,
                        label = { Text("Password Lama") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = onToggleOldPassword) {
                                Icon(
                                    imageVector = if (showOldPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = onNewPasswordChange,
                        label = { Text("Password Baru") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = onToggleNewPassword) {
                                Icon(
                                    imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = (newPassword.isNotEmpty() && newPassword.length < 6) || isSameAsOld,
                        supportingText = {
                            if (newPassword.isNotEmpty() && newPassword.length < 6) {
                                Text("Password minimal 6 karakter")
                            } else if (isSameAsOld) {
                                Text("Password baru tidak boleh sama dengan password lama")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = { Text("Konfirmasi Password Baru") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = onToggleConfirmPassword) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                                Text("Konfirmasi password tidak cocok")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Tips Password Aman",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                    Text(text = "• Panjang minimal kata sandi adalah 6 karakter", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "• Gunakan variasi kombinasi huruf unik dan angka", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "• Dilarang menggunakan password lama kembali demi keamanan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: ChangePasswordViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSuccess by viewModel.isSuccess.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showOldPassword by rememberSaveable { mutableStateOf(false) }
    var showNewPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onBack()
        }
    }

    ChangePasswordContent(
        oldPassword = oldPassword,
        newPassword = newPassword,
        confirmPassword = confirmPassword,
        showOldPassword = showOldPassword,
        showNewPassword = showNewPassword,
        showConfirmPassword = showConfirmPassword,
        isLoading = isLoading,
        error = error,
        onOldPasswordChange = { oldPassword = it },
        onNewPasswordChange = { newPassword = it },
        onConfirmPasswordChange = { confirmPassword = it},
        onToggleOldPassword = { showOldPassword = !showOldPassword },
        onToggleNewPassword = { showNewPassword = !showNewPassword },
        onToggleConfirmPassword = { showConfirmPassword = !showConfirmPassword },
        onClearError = { viewModel.clearError() },
        onBack = onBack,
        onSave = {
            viewModel.changePassword(
                oldPassword = oldPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            )
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun ChangePasswordPreview() {
    MaterialTheme {
        ChangePasswordContent(
            oldPassword = "passwordLama123",
            newPassword = "passwordBaru456",
            confirmPassword = "passwordBaru456",
            showOldPassword = false,
            showNewPassword = true,
            showConfirmPassword = false,
            isLoading = false,
            error = null,
            onOldPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onToggleOldPassword = {},
            onToggleNewPassword = {},
            onToggleConfirmPassword = {},
            onClearError = {},
            onBack = {},
            onSave = {}
        )
    }
}