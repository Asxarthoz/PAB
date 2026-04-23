
package com.example.tourtest.feature.profile.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageManager(private val context: Context) {

    private var currentPhotoUri: Uri? = null

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(imageFileName, ".jpg", context.cacheDir)
    }

    fun saveImage(bitmap: Bitmap): String {
        val file = File(context.filesDir, "profile_images/profile_${System.currentTimeMillis()}.jpg")
        file.parentFile?.mkdirs()
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        return file.absolutePath
    }

    fun loadImage(path: String): Bitmap? {
        return try {
            val file = File(path)
            if (file.exists()) {
                BitmapFactory.decodeFile(path)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    fun getCameraLauncher(onResult: (Bitmap?) -> Unit) {
        val file = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        currentPhotoUri = photoUri

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && currentPhotoUri != null) {
                val path = currentPhotoUri?.path
                if (path != null) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    onResult(bitmap)
                } else {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }

        launcher.launch(photoUri)
    }

    @Composable
    fun getGalleryLauncher(onResult: (Bitmap?) -> Unit) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                val bitmap = context.contentResolver.openInputStream(uri)
                    ?.use { BitmapFactory.decodeStream(it) }
                onResult(bitmap)
            } else {
                onResult(null)
            }
        }

        launcher.launch("image/*")
    }
}