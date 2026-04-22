
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
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
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tourtest.feature.profile.manager.ProfileManager
import kotlinx.coroutines.launch
import android.util.Patterns
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    profileManager: ProfileManager
) {
    Log.d("ProfileDebug", "=== EDIT PROFILE SCREEN OPENED ===")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentUser by profileManager.userState.collectAsStateWithLifecycle()
    val isLoading by profileManager.isLoading.collectAsStateWithLifecycle()
    val error by profileManager.error.collectAsStateWithLifecycle()
    val updateSuccess by profileManager.updateSuccess.collectAsStateWithLifecycle()
    val profileImagePath by profileManager.profileImagePath.collectAsStateWithLifecycle()

    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var nickName by remember(currentUser) { mutableStateOf(currentUser?.nickName ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }

    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(profileImagePath) {
        if (profileImagePath != null) {
            profileBitmap = profileManager.loadProfileImage(profileImagePath)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Log.d("ProfileDebug", "Foto berhasil: ${bitmap.width}x${bitmap.height}")
            profileBitmap = bitmap
            Toast.makeText(context, "Foto berhasil diambil", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("ProfileDebug", "Gagal ambil foto")
            Toast.makeText(context, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = context.contentResolver.openInputStream(it)
                ?.use { inputStream -> BitmapFactory.decodeStream(inputStream) }
            profileBitmap = bitmap
            Toast.makeText(context, "Foto berhasil dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(imageFileName, ".jpg", context.cacheDir)
    }

    DisposableEffect(Unit) {
        onDispose {
            profileManager.clearError()
            profileManager.resetUpdateSuccess()
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Profil berhasil diupdate!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profil",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (profileBitmap != null) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    profileManager.deleteProfileImage()
                                    profileBitmap = null
                                    Toast.makeText(context, "Foto dihapus", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Foto")
                        }
                    }

                    TextButton(
                        onClick = {
                            if (!isSaving) {
                                scope.launch {
                                    isSaving = true

                                    profileBitmap?.let { bitmap ->
                                        val savedPath = profileManager.saveProfileImage(bitmap)
                                        if (savedPath != null) {
                                            profileManager.updateProfileImagePath(savedPath)
                                            Toast.makeText(context, "Foto berhasil disimpan", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    profileManager.updateProfile(
                                        name = name,
                                        nickName = nickName,
                                        email = email
                                    )

                                    isSaving = false
                                }
                            }
                        },
                        enabled = !isLoading && !isSaving
                    ) {
                        if (isLoading || isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Simpan")
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
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { showImagePickerDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (profileBitmap != null) {
                    Image(
                        bitmap = profileBitmap!!.asImageBitmap(),
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { showImagePickerDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Ubah Foto",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }

            Text(
                text = "Tap untuk mengubah foto profil",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Edit informasi profil Anda",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (showImagePickerDialog) {
                AlertDialog(
                    onDismissRequest = { showImagePickerDialog = false },
                    title = { Text("Pilih Foto Profil") },
                    text = { Text("Ambil dari kamera atau pilih dari galeri") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showImagePickerDialog = false
                                cameraLauncher.launch(null)
                            }
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kamera")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showImagePickerDialog = false
                                galleryLauncher.launch("image/*")
                            }
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Galeri")
                        }
                    }
                )
            }

            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { profileManager.clearError() }) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Tutup",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        isError = name.isBlank(),
                        supportingText = {
                            if (name.isBlank()) Text("Nama lengkap tidak boleh kosong")
                        }
                    )

                    OutlinedTextField(
                        value = nickName,
                        onValueChange = { nickName = it },
                        label = { Text("Nickname") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        isError = nickName.isBlank(),
                        supportingText = {
                            if (nickName.isBlank()) Text("Nickname tidak boleh kosong")
                        }
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        isError = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                        supportingText = {
                            if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                Text("Email tidak valid")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Informasi Akun", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("• Role: ${when(currentUser?.role) {
                        "admin" -> "Administrator"
                        "mitra" -> "Mitra"
                        else -> "Wisatawan"
                    }}", fontSize = 12.sp)
                    if (currentUser?.role == "mitra") {
                        Text(
                            text = "• Status: ${if (currentUser?.isVerified == true) "Terverifikasi" else "Belum diverifikasi"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("• ID User: ${currentUser?.id?.take(8)}...", fontSize = 12.sp)
                }
            }
        }
    }
}