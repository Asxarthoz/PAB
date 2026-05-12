package com.example.tourtest.feature.wishlist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.model.Destination
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.wishlist.manager.WishlistManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel(
    application: Application,
    private val wishlistManager: WishlistManager,
    private val homepageManager: HomepageManager
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val _wishlistDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val wishlistDestinations: StateFlow<List<Destination>> = _wishlistDestinations.asStateFlow()
    private val _wishlistIds = MutableStateFlow<Set<String>>(emptySet())
    val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    fun loadWishlist() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                android.util.Log.d("WISHLIST_DEBUG", "currentUserId: $currentUserId")

                val wishes = wishlistManager.getAllWish(context)
                val favoriteIds = wishes.filter { it.userId == currentUserId }.map { it.destinationId }.toSet()
                _wishlistIds.value = favoriteIds

                val allDestinations = homepageManager.readDestinationsFromData(context)
                val favorites = allDestinations.filter { it.id in favoriteIds }
                _wishlistDestinations.value = favorites

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToWishlist(destinationId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                val success = wishlistManager.addDestination(context, currentUserId, destinationId)
                if (success) {
                    _wishlistIds.value = _wishlistIds.value + destinationId
                    val allDestinations = homepageManager.readDestinationsFromData(context)
                    val newDestination = allDestinations.find { it.id == destinationId }
                    newDestination?.let {
                        _wishlistDestinations.value = _wishlistDestinations.value + it
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeFromWishlist(destinationId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                val success = wishlistManager.removeDestination(context, currentUserId, destinationId)
                if (success) {
                    _wishlistIds.value = _wishlistIds.value - destinationId
                    _wishlistDestinations.value = _wishlistDestinations.value.filter { it.id != destinationId }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun isFavorite(destinationId: String): Boolean {
        return destinationId in _wishlistIds.value
    }

    fun clearError() {
        _error.value = null
    }
}