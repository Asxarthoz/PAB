package com.example.tourtest.feature.profile.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.model.Users
import com.example.tourtest.feature.profile.manager.ProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileManager: ProfileManager
) : ViewModel() {
    val currentUser: StateFlow<Users?> = profileManager.userState
    val isLoading : StateFlow<Boolean> = profileManager.isLoading
    val error: StateFlow<String?> = profileManager.error
    val updateSuccess: StateFlow<Boolean> = profileManager.updateSuccess
    val profileImagePath: StateFlow<String?> = profileManager.profileImagePath
    private val _profileBitmap = MutableStateFlow<Bitmap?>(null)
    val profileBitmap: StateFlow<Bitmap?> = _profileBitmap.asStateFlow()


    fun loadUser(userId: String) {
        viewModelScope.launch {
            profileManager.loadUserFromFile(userId)

            val path = profileManager.profileImagePath.value
            if (path != null) {
                val bitmap = withContext(Dispatchers.IO) {
                    profileManager.loadProfileImage(path)
                }
                _profileBitmap.value = bitmap
            }
        }
    }

    fun saveAndUpdateProfile(userId: String, name: String, nickName: String, email: String, bitmap: Bitmap?) {
        viewModelScope.launch {
            try {
                bitmap?.let { btm ->
                    profileManager.saveProfileImage(btm)
                }

                profileManager.updateProfile(
                    userId = userId,
                    name = name,
                    nickName = nickName,
                    email = email
                )
            } catch (e: Exception) {

            }
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch(Dispatchers.IO) {
            profileManager.deleteProfileImage()
            _profileBitmap.value = null
        }
    }

    fun onProfileBitmapChanged(bitmap: Bitmap?) {
        _profileBitmap.value = bitmap
    }

    fun clearError() {
        profileManager.clearError()
    }

    fun resetUpdateSuccess() {
        profileManager.resetUpdateSuccess()
    }
}