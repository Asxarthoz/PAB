package com.example.tourtest.feature.profile.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.util.Patterns
import android.util.Log
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.profile.viewmodel.ProfileViewModel
import com.example.tourtest.model.Users
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBlueMain
import com.example.tourtest.ui.theme.TourizmeTextPrimary
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    name: String,
    nickName: String,
    email: String,
    profileBitmap: Bitmap?,
    currentUser: Users?,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onNameChange: (String) -> Unit,
    onNickNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDeletePhoto: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onClearError: () -> Unit
) {
    var showImagePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Profil", fontFamily = MontserratFontFamily, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    if (profileBitmap != null) {
                        IconButton(onClick = onDeletePhoto) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Foto", tint = Color.White)
                        }
                    }

                    TextButton(onClick = onSave, enabled = !isLoading && !isSaving) {
                        if (isLoading || isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Simpan", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Simpan", fontFamily = MontserratFontFamily, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TourizmeBlueMain),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
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
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(color = Color(0xff52A0C9).copy(alpha = 0.4f))
                            .clickable { showImagePickerDialog = true },
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
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                modifier = Modifier.size(60.dp),
                                tint = TourizmeBlueMain
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = TourizmeBlueMain,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-2).dp, y = (-2).dp),
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize().clickable { showImagePickerDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Ubah Foto",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Tap untuk mengubah foto profil",
                    fontSize = 14.sp,
                    color = TourizmeBlueMain,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = MontserratFontFamily
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Edit Informasi Profil Anda",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MontserratFontFamily,
                color = TourizmeBlueMain,
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
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Nama Lengkap", fontFamily = MontserratFontFamily, fontWeight = FontWeight.Normal, color = TourizmeTextPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = TourizmeBlueMain) },
                        isError = name.isBlank(),
                        shape = RoundedCornerShape(6.dp),
                        textStyle = TextStyle(
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TourizmeTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = nickName,
                        onValueChange = onNickNameChange,
                        label = { Text("Username", fontFamily = MontserratFontFamily, fontWeight = FontWeight.Normal, color = TourizmeTextPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.AccountCircle, null, tint = TourizmeBlueMain) },
                        isError = nickName.isBlank(),
                        shape = RoundedCornerShape(6.dp),
                        textStyle = TextStyle(
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TourizmeTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email", fontFamily = MontserratFontFamily, fontWeight = FontWeight.Normal, color = TourizmeTextPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = TourizmeBlueMain) },
                        isError = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                        shape = RoundedCornerShape(6.dp),
                        textStyle = TextStyle(
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TourizmeTextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Pilih Foto Profil") },
            text = { Text("Ambil dari kamera langsung atau pilih berkas dari galeri") },
            confirmButton = {
                TextButton(onClick = { showImagePickerDialog = false; onCameraClick() }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kamera")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImagePickerDialog = false; onGalleryClick() }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galeri")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    userSession: UserSession,
    viewModel: com.example.tourtest.core.network.NetworkProfileViewModel
) {
    Log.d("ProfileDebug", "=== EDIT PROFILE SCREEN OPENED ===")
    val context = LocalContext.current

    val currentUserIdFromStore by userSession.userId.collectAsState(initial = null)
    val currentUserId = currentUserIdFromStore ?: "GUEST"

    // Ambil data dari NetworkProfileViewModel
    val currentUser by viewModel.user.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()
    val profileBitmap by viewModel.profileBitmap.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var hasFilledForm by remember { mutableStateOf(false) }

    // Load profile dari API saat screen dibuka
    LaunchedEffect(currentUserId) {
        if (currentUserId != "GUEST" && currentUserId.isNotBlank()) {
            viewModel.loadUserProfile()
        }
    }

    // Isi field form HANYA sekali saat data pertama kali tersedia
    LaunchedEffect(currentUser) {
        val user = currentUser
        if (!hasFilledForm && user != null) {
            name = user.name
            nickName = user.nickName
            email = user.email
            hasFilledForm = true
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.onProfileBitmapChanged(bitmap)
            Toast.makeText(context, "Foto berhasil diambil", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = context.contentResolver.openInputStream(it)
                ?.use { inputStream -> BitmapFactory.decodeStream(inputStream) }
            if (bitmap != null) {
                viewModel.onProfileBitmapChanged(bitmap)
                Toast.makeText(context, "Foto berhasil dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Izin kamera ditolak.", Toast.LENGTH_LONG).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearError()
            viewModel.resetUpdateSuccess()
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Profil berhasil diupdate!", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateSuccess()
            onBack()
        }
    }

    EditProfileContent(
        name = name,
        nickName = nickName,
        email = email,
        profileBitmap = profileBitmap,
        currentUser = currentUser,
        isLoading = isLoading,
        isSaving = isLoading,
        error = error,
        onNameChange = { name = it },
        onNickNameChange = { nickName = it },
        onEmailChange = { email = it },
        onBack = onBack,
        onSave = {
            if (!isLoading) {
                val imageFile: File? = profileBitmap?.toCacheFile(context)
                viewModel.saveAndUpdateProfile(
                    userId = currentUserId,
                    name = name,
                    nickName = nickName,
                    email = email,
                    bitmap = profileBitmap
                )
            }
        },
        onDeletePhoto = {
            viewModel.deleteProfileImage()
            Toast.makeText(context, "Foto dihapus", Toast.LENGTH_SHORT).show()
        },
        onCameraClick = {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        },
        onGalleryClick = {
            galleryLauncher.launch("image/*")
        },
        onClearError = {
            viewModel.clearError()
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun EditProfilePreview() {
    MaterialTheme {
        EditProfileContent(
            name = "Qoqo Altiano",
            nickName = "Qoqo",
            email = "qoqo@student.uns.ac.id",
            profileBitmap = null,
            currentUser = null,
            isLoading = false,
            isSaving = false,
            error = null,
            onNameChange = {},
            onNickNameChange = {},
            onEmailChange = {},
            onBack = {},
            onSave = {},
            onDeletePhoto = {},
            onCameraClick = {},
            onGalleryClick = {},
            onClearError = {}
        )
    }
}

// 📁 Fungsi Ekstensi Pembantu untuk Mengubah Bitmap menjadi Berkas File Sementara
fun Bitmap.toCacheFile(context: android.content.Context): File {
    val file = File(context.cacheDir, "profile_update_${System.currentTimeMillis()}.jpg")
    file.createNewFile()
    val bos = java.io.ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 85, bos)
    val bitmapData = bos.toByteArray()

    val fos = java.io.FileOutputStream(file)
    fos.write(bitmapData)
    fos.flush()
    fos.close()
    return file
}