package com.example.tourtest.feature.detaildestination.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
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
class DetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSession: UserSession,
    private val homepageManager: HomepageManager,
    private val favoriteManager: FavoriteManager,
    private val itineraryManager: ItineraryManager
) : ViewModel() {

    private var destinationId: String = ""
    private var currentUserId: String = ""

    // ── StateFlows ────────────────────────────────────────────────────────────

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

    /**
     * Ulasan milik user aktif untuk destinasi ini.
     * null  → user belum pernah mengulas
     * non-null → user sudah punya ulasan, tampilkan mode "Ulasan Anda"
     */
    private val _userReview = MutableStateFlow<Review?>(null)
    val userReview: StateFlow<Review?> = _userReview.asStateFlow()

    // ── Init ──────────────────────────────────────────────────────────────────

    fun initializeData(destinationId: String) {
        if (this.destinationId == destinationId) return
        this.destinationId = destinationId

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                currentUserId = userSession.userId.firstOrNull() ?: "GUEST"
                refreshDestination()
                loadStatus()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Muat ulang data destinasi dari storage, lalu perbarui userReview.
     * Dipanggil setelah setiap aksi ADD / UPDATE / DELETE.
     */
    private fun refreshDestination() {
        val dest = homepageManager.getDestinationById(context, destinationId)
        _destination.value = dest
        checkUserReviewStatus()
    }

    private fun loadStatus() {
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

    /**
     * Cek apakah user aktif sudah punya ulasan untuk destinasi ini.
     * Perbarui _userReview → UI otomatis recompose.
     */
    private fun checkUserReviewStatus() {
        if (currentUserId.isBlank() || currentUserId == "GUEST") {
            _userReview.value = null
            return
        }
        _userReview.value = homepageManager.getUserReview(context, destinationId, currentUserId)
    }

    // ── Public actions ────────────────────────────────────────────────────────

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
                if (success) _isPlanned.value = true
                else _error.value = "Gagal menambahkan ke rencana"
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * TAMBAH ulasan baru.
     * Hanya berhasil jika user belum punya ulasan (userReview == null).
     */
    fun submitReview(rating: Float, comment: String) {
        if (comment.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uId   = userSession.userId.firstOrNull()   ?: "GUEST"
                val uName = userSession.username.firstOrNull() ?: "User Tourizme"

                val added = homepageManager.addReview(
                    context  = context,
                    destId   = destinationId,
                    userId   = uId,
                    userName = uName,
                    rating   = rating.coerceIn(1f, 5f),
                    comment  = comment.trim()
                )
                if (added) {
                    homepageManager.clearCache()
                    refreshDestination()
                } else {
                    _error.value = "Anda sudah memberikan ulasan. Gunakan tombol Edit untuk mengubahnya."
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * EDIT ulasan yang sudah ada.
     * Mencari baris lama via destId+userId, menggantikan dengan data baru.
     */
    fun updateReview(newRating: Float, newComment: String) {
        if (newComment.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uId   = userSession.userId.firstOrNull()   ?: "GUEST"
                val uName = userSession.username.firstOrNull() ?: "User Tourizme"

                homepageManager.updateReview(
                    context    = context,
                    destId     = destinationId,
                    userId     = uId,
                    userName   = uName,
                    newRating  = newRating.coerceIn(1f, 5f),
                    newComment = newComment.trim()
                )
                homepageManager.clearCache()
                refreshDestination()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * HAPUS ulasan user aktif.
     * Setelah berhasil, userReview → null sehingga form kembali ke mode input baru.
     */
    fun deleteMyReview() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uId = userSession.userId.firstOrNull() ?: "GUEST"
                homepageManager.deleteReview(context, destinationId, uId)
                homepageManager.clearCache()
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
