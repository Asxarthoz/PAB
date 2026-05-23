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
import com.example.tourtest.feature.profile.manager.ProfileManager
import android.util.Patterns
import android.util.Log
import androidx.activity.result.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.model.Users
import kotlinx.coroutines.launch

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
                title = { Text(text = "Edit Profil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (profileBitmap != null) {
                        IconButton(onClick = onDeletePhoto) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Foto", tint = MaterialTheme.colorScheme.error)
                        }
                    }

                    TextButton(onClick = onSave, enabled = !isLoading && !isSaving) {
                        if (isLoading || isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Simpan")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Simpan")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)), // Selaras dengan ProfileScreen
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
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Ikon Kamera diletakkan di luar lingkaran agar posisinya pas di pojok kanan bawah
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
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

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tap untuk mengubah foto profil",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Edit Informasi Profil Anda",
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
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        isError = name.isBlank()
                    )

                    OutlinedTextField(
                        value = nickName,
                        onValueChange = onNickNameChange,
                        label = { Text("Nickname") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
                        isError = nickName.isBlank()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        isError = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
    profileManager: ProfileManager
) {
    Log.d("ProfileDebug", "=== EDIT PROFILE SCREEN OPENED ===")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentUserIdFromStore by userSession.userId.collectAsState(initial = null)
    val currentUser by profileManager.userState.collectAsStateWithLifecycle()
    val isLoading by profileManager.isLoading.collectAsStateWithLifecycle()
    val error by profileManager.error.collectAsStateWithLifecycle()
    val updateSuccess by profileManager.updateSuccess.collectAsStateWithLifecycle()
    val profileImagePath by profileManager.profileImagePath.collectAsStateWithLifecycle()

//    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
//    var nickName by remember(currentUser) { mutableStateOf(currentUser?.nickName ?: "") }
//    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
//    var showImagePickerDialog by remember { mutableStateOf(false) }
//    var isSaving by rememberSaveable { mutableStateOf(false) }
//
//    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
//    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    var name by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(currentUserIdFromStore) {
        val currentUserId = currentUserIdFromStore
        if (currentUserId != null && currentUserId != "GUEST" && currentUserId.isNotBlank()) {
            profileManager.loadUserFromFile(currentUserId)
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name
            nickName = user.nickName
            email = user.email
        }
    }

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

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Izin kamera ditolak. Gagal mengambil gambar.", Toast.LENGTH_LONG).show()
        }
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
        isSaving = isSaving,
        error = error,
        onNameChange = { name = it},
        onNickNameChange = { nickName = it },
        onEmailChange = { email = it },
        onBack = onBack,
        onSave = {
            if (!isSaving) {
                val userId = currentUserIdFromStore ?: "GUEST"
                if (userId != null || userId != "GUEST") {
                    scope.launch {
                        isSaving = true
                        try {
                            profileBitmap?.let { bitmap ->
                                val savedPath = profileManager.saveProfileImage(bitmap)
                                if (savedPath != null) {
                                    profileManager.updateProfileImagePath(savedPath)
                                    Toast.makeText(
                                        context,
                                        "Foto berhasil disimpan",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }

                            profileManager.updateProfile(
                                userId = userId,
                                name = name,
                                nickName = nickName,
                                email = email
                            )
                        } catch (e: Exception) {
                            Log.e("ProfileDebug", "Save error", e)
                        } finally {
                            isSaving = false
                        }
                    }
                }

            }
        },
        onDeletePhoto = {
            scope.launch {
                profileManager.deleteProfileImage()
                profileBitmap = null
                Toast.makeText(context, "Foto dihapus", Toast.LENGTH_SHORT).show()
            }
        },
        onCameraClick = {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)        },
        onGalleryClick = {
            galleryLauncher.launch("image/*")
        },
        onClearError = {
            profileManager.clearError()
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