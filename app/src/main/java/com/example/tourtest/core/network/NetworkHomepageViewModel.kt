package com.example.tourtest.core.network

import android.content.Context
import kotlinx.coroutines.flow.first
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NetworkHomepageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSession: UserSession,
    private val networkApiManager: NetworkApiManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allDestinations = MutableStateFlow<List<Destination>>(emptyList())
    private val _filteredDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val filteredDestinations: StateFlow<List<Destination>> = _filteredDestinations.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDestinations()
    }

    fun loadDestinations() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("🔍 loadDestinations() dipanggil")
                val destinations = networkApiManager.getDestinations()
                println("🔍 getDestinations() selesai, size: ${destinations.size}")

                _allDestinations.value = destinations
                _filteredDestinations.value = destinations
                _isLoading.value = false

            } catch (e: Exception) {
                println("🔴 ERROR loadDestinations: ${e.message}")
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _isSearchActive.value = query.isNotBlank()
        filterDestinations()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _isSearchActive.value = false
        _filteredDestinations.value = _allDestinations.value
    }

    private fun filterDestinations() {
        val query = _searchQuery.value.lowercase().trim()
        if (query.isEmpty()) {
            _filteredDestinations.value = _allDestinations.value
        } else {
            val filtered = _allDestinations.value.filter {
                it.name.lowercase().contains(query) ||
                        it.location.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
            }
            _filteredDestinations.value = filtered
        }
    }

    fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    fun refresh() {
        loadDestinations()
    }

    fun addToWishlist(destinationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = userSession.token.first() ?: ""
                val idInLong = destinationId.toLongOrNull() ?: 0L

                if (token.isBlank()) {
                    println("⚠️ addToWishlist - token kosong")
                    return@launch
                }

                println("🚀 addToWishlist -> ID: $idInLong")
                val isSuccess = networkApiManager.addToWishlist(token, idInLong)
                println(if (isSuccess) "✅ Wishlist berhasil disimpan" else "❌ Wishlist gagal disimpan")

            } catch (e: Exception) {
                println("🔴 Error addToWishlist: ${e.message}")
            }
        }
    }
}