package com.example.tourtest.feature.profile.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.feature.profile.viewmodel.ProfileViewModel
import com.example.tourtest.model.Users
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    currentUser: Users?,
    profileBitmap: Bitmap?,
    isLoading: Boolean,
    error: String?,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onLogoutClick: () -> Unit,
    onImageClick: () -> Unit,
    onBackToLogin: () -> Unit
    ) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Wisatawan") },
                actions = {
                    if (currentUser != null) {
                        IconButton(
                            onClick = onEditProfile
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit Profil"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Memuat data profil...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onBackToLogin) {
                            Text("Kembali ke Login")
                        }
                    }
                }
            }
            currentUser != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                if (profileBitmap != null) {
                                    onImageClick()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap!!.asImageBitmap(),
                                contentDescription = "Foto Profil",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ProfileItem(
                                label = "Nama Lengkap",
                                value = currentUser!!.name,
                                icon = Icons.Rounded.Person
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            ProfileItem(
                                label = "Nickname",
                                value = currentUser!!.nickName,
                                icon = Icons.Rounded.Person
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            ProfileItem(
                                label = "Alamat Email",
                                value = currentUser!!.email,
                                icon = Icons.Rounded.Email
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            val maskedPassword = "*".repeat(currentUser!!.password.length).ifEmpty { "********" }
                            ProfileItem(
                                label = "Kata Sandi",
                                value = maskedPassword,
                                icon = Icons.Rounded.Lock
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Role",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = when(currentUser!!.role) {
                                        "admin" -> "Administrator"
                                        "mitra" -> "Mitra"
                                        else -> "Wisatawan"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (currentUser!!.role == "mitra") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Status Verifikasi",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (currentUser!!.isVerified) "✓ Terverifikasi" else "✗ Belum diverifikasi",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (currentUser!!.isVerified)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedButton(
                        onClick = onChangePassword,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Rounded.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ganti Password")
                    }

                    Button(
                        onClick = onLogoutClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Keluar dari Akun")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileGuestContent(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Wisatawan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TourizmeEmptyState(
                message = "Anda belum Login",
                subMessage = "Silahkan Login untuk melihat informasi profil anda",
                imageVector = Icons.Default.Lock,
                actionButton = {
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Login Sekarang!")
                    }
                }
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToFullScreenImage: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val profileBitmap by viewModel.profileBitmap.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        viewModel.loadUser()
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < 500) {
            delay(500 - elapsed)
        }
    }

//    LaunchedEffect(currentUser) {
//        if (currentUser?.profileImage != null) {
//            profileBitmap = profileManager.loadProfileImage(currentUser!!.profileImage)
//        }
//    }

    if(showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Konfirmasi Keluar") },
            text = { Text(text = "Yakin ingin Keluar dari Akun?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }

                ) {
                    Text("Keluar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                }) {
                    Text(text = "Batal")
                }
            }
        )
    }

    if (currentUser?.id == "GUEST" || currentUser?.id.isNullOrBlank()) {
        ProfileGuestContent(
            onBack = onLogout,
            onNavigateToLogin = onLogout
        )
    } else {
        ProfileContent(
            currentUser = currentUser,
            profileBitmap = profileBitmap,
            isLoading = isLoading,
            error = error,
            onEditProfile = onNavigateToEditProfile,
            onChangePassword = onNavigateToChangePassword,
            onLogoutClick = { showLogoutDialog = true },
            onImageClick = onNavigateToFullScreenImage,
            onBackToLogin = onLogout
        )
    }
}

@Composable
fun ProfileItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfilePreview() {
    MaterialTheme {
        ProfileContent(
            currentUser = Users(
                id = "123",
                name = "Qoqo Altiano",
                nickName = "Qoqo",
                email = "qoqo@student.uns.ac.id",
                password = "password123",
                role = "wisatawan",
                isVerified = true
            ),
            profileBitmap = null,
            isLoading = false,
            error = null,
            onEditProfile = {},
            onChangePassword = {},
            onLogoutClick = {},
            onImageClick = {},
            onBackToLogin = {}
        )
    }
}