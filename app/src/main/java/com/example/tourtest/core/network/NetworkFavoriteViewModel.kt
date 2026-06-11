package com.example.tourtest.core.network

import android.content.Context
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkFavoriteViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSession: UserSession,
    private val networkApiManager: NetworkApiManager
) : ViewModel() {

    private val _favoriteDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val favoriteDestinations: StateFlow<List<Destination>> = _favoriteDestinations.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allFavorites: List<Destination> = emptyList()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterFavorites()
    }

    fun loadFavorite() {

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                // STEP 1: Ambil token dari UserSession
                println("📌 STEP 1: Mengambil token dari UserSession...")
                val token = userSession.token.firstOrNull()
                println("🔑 TOKEN DARI SESSION: $token")
                println("🔑 PANJANG TOKEN: ${token?.length ?: 0} karakter")
                println("🔑 APAKAH TOKEN NULL? ${token == null}")
                println("🔑 APAKAH TOKEN BLANK? ${token.isNullOrBlank()}")

                if (token.isNullOrBlank()) {
                    println("❌❌❌ TOKEN KOSONG - User belum login! ❌❌❌")
                    _isLoading.value = false
                    return@launch
                }

                println("✅ TOKEN VALID, lanjut ke STEP 2...")

                // STEP 2: Panggil API getWishlist
                println("📌 STEP 2: Memanggil networkApiManager.getWishlist()...")
                val wishlist = networkApiManager.getWishlist(token)

                println("📌 STEP 3: Memproses response wishlist...")
                println("📦 WISHLIST SIZE: ${wishlist.size}")
                wishlist.forEachIndexed { index, item ->
                    println("   Wishlist $index: id=${item.id}, destinationId=${item.destinationId}")
                }

                // STEP 3: Ambil detail destination untuk setiap wishlist
                println("📌 STEP 4: Mengambil detail destination untuk setiap wishlist...")
                val destinations = wishlist.mapNotNull { wishlistItem ->
                    println("   Mengambil destination untuk ID: ${wishlistItem.destinationId}")
                    val dest = networkApiManager.getDestinationById(wishlistItem.destinationId)
                    if (dest == null) {
                        println("   ❌ Destination dengan ID ${wishlistItem.destinationId} tidak ditemukan!")
                    } else {
                        println("   ✅ Destination ditemukan: ${dest.name}")
                    }
                    dest
                }

                println("📌 STEP 5: Menyimpan data ke StateFlow...")
                allFavorites = destinations
                _favoriteDestinations.value = destinations
                _favoriteIds.value = destinations.map { it.id }.toSet()

                println("📊 FINAL RESULT:")
                println("   - Total favorites: ${destinations.size}")
                println("   - Favorite IDs: ${_favoriteIds.value}")
                println("   - First destination: ${destinations.firstOrNull()?.name ?: "Tidak ada"}")

                filterFavorites()

            } catch (e: Exception) {
                println("❌❌❌ ERROR di loadFavorite: ${e.message} ❌❌❌")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorite(destinationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = userSession.token.firstOrNull()
                if (token.isNullOrBlank()) {
                    println("❌ ADD FAVORITE - TOKEN KOSONG")
                    return@launch
                }

                val success = networkApiManager.addToWishlist(token, destinationId.toLong())
                if (success) {
                    loadFavorite()
                }
            } catch (e: Exception) {
                println("❌ ERROR addToFavorite: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun removeFromFavorite(destinationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = userSession.token.firstOrNull()
                if (token.isNullOrBlank()) {
                    println("❌ REMOVE FAVORITE - TOKEN KOSONG")
                    return@launch
                }

                val wishlist = networkApiManager.getWishlist(token)
                val wishlistItem = wishlist.find { it.destinationId.toString() == destinationId }
                wishlistItem?.let {
                    networkApiManager.removeFromWishlist(token, it.id)
                }
                loadFavorite()
            } catch (e: Exception) {
                println("❌ ERROR removeFromFavorite: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun filterFavorites() {
        val query = _searchQuery.value.lowercase().trim()
        if (query.isEmpty()) {
            _favoriteDestinations.value = allFavorites
        } else {
            val filtered = allFavorites.filter {
                it.name.lowercase().contains(query) ||
                        it.location.lowercase().contains(query)
            }
            _favoriteDestinations.value = filtered
        }
    }
}