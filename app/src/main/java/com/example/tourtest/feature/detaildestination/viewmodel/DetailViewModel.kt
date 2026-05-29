package com.example.tourtest.feature.detaildestination.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
//    private val savedStateHandle: SavedStateHandle,
    private val userSession: UserSession,
    private val homepageManager: HomepageManager,
    private val favoriteManager: FavoriteManager,
    private val itineraryManager: ItineraryManager
) : ViewModel() {

    private var destinationId: String = ""

    private var currentUserId: String = ""

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

    fun initializeData(destinationId: String) {
        if (this.destinationId == destinationId) return
        this.destinationId = destinationId

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                currentUserId = userSession.userId.firstOrNull() ?: "GUEST"

                loadDestination()
                loadStatus()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadDestination() {
        val dest = homepageManager.getDestinationById(context, destinationId)
        _destination.value = dest
    }

    private suspend fun loadStatus() {
        try {
            val favorites = favoriteManager.getAllFavorite(context)
            _isFavorite.value = favorites.any {
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

    fun toggleFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_isFavorite.value) {
                    favoriteManager.removeDestination(context, currentUserId, destinationId)
                } else {
                    favoriteManager.addDestination(context, currentUserId, destinationId)
                }
                _isFavorite.value = !_isFavorite.value
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addToItinerary(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
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