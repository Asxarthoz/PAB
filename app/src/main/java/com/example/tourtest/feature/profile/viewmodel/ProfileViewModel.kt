package com.example.tourtest.feature.profile.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.model.Users
import com.example.tourtest.feature.profile.manager.ProfileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val profileManager: ProfileManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Users?>(null)
    val currentUser: StateFlow<Users?> = _currentUser.asStateFlow()
    private val _profileBitmap = MutableStateFlow<Bitmap?>(null)
    val profileBitmap: StateFlow<Bitmap?> = _profileBitmap.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                withContext(Dispatchers.IO) {
                    profileManager.loadUserFromFile(userId)
                }

                val user = profileManager.userState.value
                _currentUser.value = user

                user?.profileImage?.let { path ->
                    withContext(Dispatchers.IO) {
                        val bitmap = profileManager.loadProfileImage(path)
                        _profileBitmap.value = bitmap
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                delay(300)
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, nickName: String, email: String) {
        viewModelScope.launch {
            val success = profileManager.updateProfile(name, nickName, email)
            _updateSuccess.value = success
            if (!success) {
                _error.value = profileManager.error.value
            }
        }
    }

    fun saveProfileImage(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = profileManager.saveProfileImage(bitmap)
            path?.let {
                _profileBitmap.value = bitmap
            }
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch {
            profileManager.deleteProfileImage()
            _profileBitmap.value = null
        }
    }

    fun clearError() {
        _error.value = null
        profileManager.clearError()
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
        profileManager.resetUpdateSuccess()
    }
}