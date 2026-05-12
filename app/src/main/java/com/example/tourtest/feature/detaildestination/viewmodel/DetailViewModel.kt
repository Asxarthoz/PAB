package com.example.tourtest.feature.detaildestination.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.wishlist.manager.WishlistManager
import com.example.tourtest.model.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    application: Application,
    private val destinationId: String,
    private val currentUserId: String,
    private val wishlistManager: WishlistManager,
    private val itineraryManager: ItineraryManager
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val _destination = MutableStateFlow<Destination?>(null)
    val destination: StateFlow<Destination?> = _destination.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    private val _isPlanned = MutableStateFlow(false)
    val isPlanned: StateFlow<Boolean> = _isPlanned.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDestination()
        loadStatus()
    }

    private fun loadDestination() {
        viewModelScope.launch {
            _isLoading.value = true
            val dest = HomepageManager.getDestinationById(context, destinationId)
            _destination.value = dest
            _isLoading.value = false
        }
    }

    private fun loadStatus() {
        viewModelScope.launch {
            try {
                val wishes = wishlistManager.getAllWish(context)
                _isFavorite.value = wishes.any {
                    it.destinationId == destinationId && it.userId == currentUserId
                }

                val itineraries = itineraryManager.getAllItinerary(context)
                _isPlanned.value = itineraries.any {
                    it.destinationId == destinationId && it.userId == currentUserId
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                if (_isFavorite.value) {
                    wishlistManager.removeDestination(context, currentUserId, destinationId)
                } else {
                    wishlistManager.addDestination(context, currentUserId, destinationId)
                }
                _isFavorite.value = !_isFavorite.value
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addToItinerary(date: String) {
        viewModelScope.launch {
            try {
                val success = itineraryManager.addDestination(context, currentUserId, destinationId, date)
                if (success) {
                    _isPlanned.value = true
                } else {
                    _error.value = "Gagal menambahkan ke rencana"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}