package com.example.tourtest.core.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.model.Destination
import com.example.tourtest.model.Review
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
class NetworkDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSession: UserSession,
    private val networkApiManager: NetworkApiManager
) : ViewModel() {

    private var destinationIdLong: Long = 0L
    private var currentToken: String = ""
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

    private val _userReview = MutableStateFlow<Review?>(null)
    val userReview: StateFlow<Review?> = _userReview.asStateFlow()

    // ========== INITIALIZATION ==========

    fun initializeData(destinationId: String) {
        val idLong = destinationId.toLongOrNull() ?: 0L
        if (destinationIdLong == idLong && idLong != 0L) return

        this.destinationIdLong = idLong

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                currentToken = userSession.token.firstOrNull() ?: ""
                currentUserId = userSession.userId.firstOrNull() ?: ""
                refreshDestination()
                loadStatus()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun refreshDestination() {
        val dest = networkApiManager.getDestinationById(destinationIdLong)
        _destination.value = dest
        checkUserReviewStatus()
    }

    private suspend fun loadStatus() {
        if (currentToken.isBlank() || currentUserId == "GUEST") return
        try {
            // Cek wishlist dari API
            val wishlist = networkApiManager.getWishlist(currentToken)
            _isFavorite.value = wishlist.any { it.destinationId == destinationIdLong }

            // Cek itinerary dari API
            val itineraries = networkApiManager.getItineraries(currentToken)
            _isPlanned.value = itineraries.any { itinerary ->
                itinerary.items?.any { item -> item.destinationId == destinationIdLong } == true
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    private fun checkUserReviewStatus() {
        if (currentUserId.isBlank() || currentUserId == "GUEST") {
            _userReview.value = null
            return
        }
        // API review tidak selalu sertakan user_id, coba cocokkan dengan userId atau username
        val currentUsername = _destination.value?.reviews?.find {
            it.userId == currentUserId
        }
        _userReview.value = currentUsername
    }

    // ========== FAVORITE & ITINERARY ==========

    fun toggleFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentToken.isBlank() || currentUserId == "GUEST") {
                    _error.value = "Silakan login terlebih dahulu"
                    return@launch
                }

                val destinationIdStr = destinationIdLong.toString()

                if (_isFavorite.value) {
                    // Ambil wishlist lalu cari id-nya untuk dihapus
                    val wishlist = networkApiManager.getWishlist(currentToken)
                    val wishlistItem = wishlist.find { it.destinationId.toString() == destinationIdStr }
                    if (wishlistItem != null) {
                        val success = networkApiManager.removeFromWishlist(currentToken, wishlistItem.id)
                        if (success) _isFavorite.value = false
                    }
                } else {
                    val success = networkApiManager.addToWishlist(currentToken, destinationIdLong)
                    if (success) _isFavorite.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addToItinerary(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentToken.isBlank() || currentUserId == "GUEST") {
                    _error.value = "Silakan login terlebih dahulu"
                    return@launch
                }

                // Step 1: Buat itinerary baru
                val itinerary = networkApiManager.createItinerary(
                    token = currentToken,
                    title = "Perjalanan saya",
                    startDate = date
                )
                if (itinerary == null) {
                    _error.value = "Gagal membuat rencana perjalanan"
                    return@launch
                }

                // Step 2: Tambahkan destinasi sebagai item
                val itemAdded = networkApiManager.addItineraryItem(
                    token = currentToken,
                    itineraryId = itinerary.id,
                    destinationId = destinationIdLong,
                    day = 1,
                    sequenceOrder = 1,
                    startTime = "08:00",
                    endTime = "10:00"
                )

                if (itemAdded) {
                    _isPlanned.value = true
                } else {
                    _error.value = "Gagal menambahkan destinasi ke rencana"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // ========== REVIEW ==========

    fun submitReview(rating: Float, comment: String) {
        if (comment.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentToken.isBlank() || currentUserId == "GUEST") {
                    _error.value = "Silakan login terlebih dahulu"
                    return@launch
                }

                // ✅ createReview dengan token
                val success = networkApiManager.createReview(
                    token = currentToken,
                    destinationId = destinationIdLong,
                    rating = rating.toInt().coerceIn(1, 5),
                    description = comment.trim()
                )

                if (success) {
                    networkApiManager.clearCache()
                    refreshDestination()
                } else {
                    _error.value = "Gagal mengirim ulasan ke server."
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateReview(newRating: Float, newComment: String) {
        if (newComment.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _error.value = "Fitur update review akan tersedia setelah API aktif"
                networkApiManager.clearCache()
                refreshDestination()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteMyReview() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _error.value = "Fitur hapus review akan tersedia setelah API aktif"
                networkApiManager.clearCache()
                refreshDestination()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}