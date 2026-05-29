package com.example.tourtest.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.feature.profile.manager.PasswordManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val passwordManager: PasswordManager
) : ViewModel(){
    val isLoading: StateFlow<Boolean> = passwordManager.isLoading
    val isSuccess: StateFlow<Boolean> = passwordManager.isSuccess
    val error: StateFlow<String?> = passwordManager.error

    fun loadCurrentUser() {
        viewModelScope.launch {
            passwordManager.loadCurrentUser()
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            passwordManager.changePassword(oldPassword = oldPassword, newPassword = newPassword, confirmPassword = confirmPassword)
        }
    }

    fun clearError() {
        passwordManager.clearError()
    }

    fun resetState() {
        passwordManager.resetState()
    }
}