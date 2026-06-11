package com.example.tourtest.core.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.model.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class NetworkProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,  // ← tambah import ini
    private val userSession: UserSession,
    private val networkApiManager: NetworkApiManager
) : ViewModel() {

    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _profileImagePath = MutableStateFlow<String?>(null)
    val profileImagePath: StateFlow<String?> = _profileImagePath.asStateFlow()

    private val _profileBitmap = MutableStateFlow<Bitmap?>(null)
    val profileBitmap: StateFlow<Bitmap?> = _profileBitmap.asStateFlow()

    fun loadUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val token = userSession.token.firstOrNull()
                println("🔑 TOKEN PROFILE: ${token?.take(30)}")

                if (token.isNullOrBlank()) {
                    println("❌ TOKEN KOSONG")
                    return@launch
                }

                val userResponse = networkApiManager.getCurrentUser(token)
                if (userResponse != null) {
                    val users = Users(
                        id = userResponse.id.toString(),
                        name = userResponse.fullname,
                        nickName = userResponse.username,
                        email = userResponse.email,
                        password = "",
                        role = userResponse.role,
                        isVerified = false,
                        profileImage = userResponse.profilePicture
                    )
                    _user.value = users

                    // Load profile image if exists
                    userResponse.profilePictureUrl?.let { url ->
                        _profileImagePath.value = url
                    }
                    println("✅ PROFILE LOADED: ${userResponse.fullname}")
                }            } catch (e: Exception) {
                _error.value = e.message
                println("❌ ERROR: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveAndUpdateProfile(userId: String, name: String, nickName: String, email: String, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val token = userSession.token.firstOrNull()
                if (token.isNullOrBlank()) {
                    _error.value = "Token tidak ditemukan"
                    return@launch
                }

                val success = networkApiManager.updateProfile(
                    token = token,
                    name = name,
                    email = email
                )

                bitmap?.let {
                    saveProfileImageLocally(it)
                }

                if (success) {
                    _updateSuccess.value = true
                    loadUserProfile()
                } else {
                    _error.value = "Gagal memperbarui profil"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveProfileImageLocally(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, "profile_image.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                _profileImagePath.value = file.absolutePath
                _profileBitmap.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = _profileImagePath.value?.let { File(it) }
                file?.delete()
                _profileImagePath.value = null
                _profileBitmap.value = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadProfileImage(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(path)
                _profileBitmap.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onProfileBitmapChanged(bitmap: Bitmap?) {
        _profileBitmap.value = bitmap
    }

    fun clearError() {
        _error.value = null
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun refresh() {
        loadUserProfile()
    }
}