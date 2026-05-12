package com.example.tourtest.feature.favorite.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.model.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(
    application: Application,
    private val favoriteManager: FavoriteManager,
    private val homepageManager: HomepageManager
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val _favoriteDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val favoriteDestinations: StateFlow<List<Destination>> = _favoriteDestinations.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadFavorite() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                Log.d("FAVORITE_DEBUG", "currentUserId: $currentUserId")

                // Mengasumsikan nama method di manager juga sudah disesuaikan atau tetap dipanggil
                val favoritesData = favoriteManager.getAllFavorite(context)
                val favoriteIds = favoritesData.filter { it.userId == currentUserId }.map { it.destinationId }.toSet()
                _favoriteIds.value = favoriteIds

                val allDestinations = homepageManager.readDestinationsFromData(context)
                val favorites = allDestinations.filter { it.id in favoriteIds }
                _favoriteDestinations.value = favorites

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorite(destinationId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                val success = favoriteManager.addDestination(context, currentUserId, destinationId)
                if (success) {
                    _favoriteIds.value = _favoriteIds.value + destinationId
                    val allDestinations = homepageManager.readDestinationsFromData(context)
                    val newDestination = allDestinations.find { it.id == destinationId }
                    newDestination?.let {
                        _favoriteDestinations.value = _favoriteDestinations.value + it
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeFromFavorite(destinationId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                val success = favoriteManager.removeDestination(context, currentUserId, destinationId)
                if (success) {
                    _favoriteIds.value = _favoriteIds.value - destinationId
                    _favoriteDestinations.value = _favoriteDestinations.value.filter { it.id != destinationId }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun isFavorite(destinationId: String): Boolean {
        return destinationId in _favoriteIds.value
    }

    fun clearError() {
        _error.value = null
    }
}