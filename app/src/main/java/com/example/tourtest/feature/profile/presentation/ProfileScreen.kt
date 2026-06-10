package com.example.tourtest.feature.profile.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.ChevronRight
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
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.profile.viewmodel.ProfileViewModel
import com.example.tourtest.model.Users
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBlueMain
import com.example.tourtest.ui.theme.TourizmeTextPrimary

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
                title = { Text("Profil Wisatawan", fontFamily = MontserratFontFamily, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp,) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TourizmeBlueMain
                ),
                windowInsets = WindowInsets(0.dp)
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
                val user = currentUser

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(color = Color(0xff52A0C9).copy(alpha = 0.4f))
                                .clickable { profileBitmap?.let { bitmap ->
                                    onImageClick() }
                                           },
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileBitmap != null) {
                                Image(
                                    bitmap = profileBitmap.asImageBitmap(),
                                    contentDescription = "Foto Profil",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = TourizmeBlueMain
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = user.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MontserratFontFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "@${user.nickName.ifEmpty { "wisatawan" }}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        val roleLabel = when (user.role) {
                            "admin" -> "Administrator"
                            "mitra" -> if (user.isVerified) "Mitra Terverifikasi" else "Mitra (Belum Verifikasi)"
                            else -> "Wisatawan"
                        }
                        val badgeColor = if (user.role == "mitra" && !user.isVerified)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer

                        Surface(
                            modifier = Modifier.width(150.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = TourizmeBlueMain,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = roleLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = MontserratFontFamily,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Informasi Akun",
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = TourizmeBlueMain,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            ProfileRowItem(
                                label = "Email",
                                value = user.email,
                                icon = Icons.Rounded.Email
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                            ProfileRowItem(
                                label = "Kata Sandi",
                                value = "*".repeat(user.password.length).ifEmpty { "********" },
                                icon = Icons.Rounded.Lock
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pengaturan",
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = TourizmeBlueMain,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            ActionRowItem(
                                title = "Edit Data Profil",
                                icon = Icons.Rounded.Edit,
                                onClick = onEditProfile
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                            ActionRowItem(
                                title = "Ubah Kata Sandi",
                                icon = Icons.Rounded.Lock,
                                onClick = onChangePassword
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Button(
                            onClick = onLogoutClick,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB0251B),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Keluar dari Akun", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

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
                title = { Text("Profil Wisatawan", fontFamily = MontserratFontFamily, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp) },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TourizmeBlueMain),
                windowInsets = WindowInsets(0.dp)
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
                        modifier = Modifier.fillMaxWidth(0.7f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TourizmeBlueMain,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Login Sekarang!", fontFamily = MontserratFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
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
    userSession: UserSession,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToFullScreenImage: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUserIdFromStore by userSession.userId.collectAsState(initial = null)
    val currentUserId = currentUserIdFromStore ?: "GUEST"

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val profileBitmap by viewModel.profileBitmap.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUserIdFromStore) {
        currentUserIdFromStore?.let { id ->
            if (id != "GUEST" && id.isNotBlank()) {
                viewModel.loadUser(id)
            }
        }
    }


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

    if (currentUserId == "GUEST" || currentUserId.isBlank()) {
        ProfileGuestContent(
            onBack = onBack,
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
fun ProfileRowItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TourizmeBlueMain
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = TourizmeBlueMain, fontFamily = MontserratFontFamily, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 16.sp, color = TourizmeTextPrimary , fontFamily = MontserratFontFamily , fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ActionRowItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TourizmeBlueMain)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TourizmeBlueMain)
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TourizmeBlueMain
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfilePreview() {
    MaterialTheme {
        ProfileContent(
            currentUser = Users(
                id = "123",
                name = "Zidan Dicky Ambasuha",
                nickName = "Zidan",
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

@Preview(showSystemUi = true)
@Composable
fun ProfileGuestPreview() {
    MaterialTheme {
        ProfileGuestContent(
            onBack = {},
            onNavigateToLogin = {}
        )
    }
}