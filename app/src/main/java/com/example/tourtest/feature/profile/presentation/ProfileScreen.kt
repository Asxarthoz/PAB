// feature/profile/presentation/ProfileScreen.kt
package com.example.tourtest.feature.profile.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import com.example.tourtest.model.Users
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val profileManager = remember { ProfileManager(context) }

    var currentUser by remember { mutableStateOf<Users?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State untuk foto profil
    val profileImagePath by profileManager.profileImagePath.collectAsStateWithLifecycle()
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load data user
    LaunchedEffect(Unit) {
        isLoading = true

        val userId = AuthManager.getCurrentUserId()
        if (userId != null) {
            val user = AuthManager.getUserById(context, userId)
            currentUser = user
            if (user == null) {
                errorMessage = "User tidak ditemukan"
            }
        } else {
            errorMessage = "Belum ada user yang login"
        }

        isLoading = false
    }

    // Load foto profil jika ada
    LaunchedEffect(currentUser) {
        if (currentUser?.profileImage != null) {
            profileBitmap = profileManager.loadProfileImage(currentUser!!.profileImage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Wisatawan") },
                actions = {
                    if (navController != null && currentUser != null) {
                        IconButton(
                            onClick = { navController.navigate("edit_profile") }
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
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onLogout) {
                            Text("Kembali ke Login")
                        }
                    }
                }
            }
            currentUser != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar dengan foto profil
                    // Avatar dengan foto profil (bisa diklik untuk full screen)
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                if (profileBitmap != null && navController != null) {
                                    navController.navigate("full_screen_image")
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

                    // Card Profil
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


                            if (currentUser?.role == "mitra") {
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

                    Spacer(modifier = Modifier.weight(1f))

                    if (navController != null) {
                        OutlinedButton(
                            onClick = { navController.navigate("change_password") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Rounded.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ganti Password")
                        }
                    }

                    Button(
                        onClick = onLogout,
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