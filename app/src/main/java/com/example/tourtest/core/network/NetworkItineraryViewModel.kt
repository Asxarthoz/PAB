package com.example.tourtest.core.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.model.Destination
import com.example.tourtest.model.Itinerary
import com.example.tourtest.model.ItineraryWithDestination
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
class NetworkItineraryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSession: UserSession,
    private val networkApiManager: NetworkApiManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allItineraries = MutableStateFlow<List<ItineraryWithDestination>>(emptyList())
    private val _groupedItinerary = MutableStateFlow<Map<String, List<ItineraryWithDestination>>>(emptyMap())
    val groupedItinerary: StateFlow<Map<String, List<ItineraryWithDestination>>> = _groupedItinerary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterItineraries()
    }

    fun loadItineraries() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null
            try {
                // ✅ AMBIL TOKEN SETIAP KALI
                val token = userSession.token.firstOrNull()
                println("🔑 TOKEN ITINERARY: ${token?.take(30)}")

                if (token.isNullOrBlank()) {
                    println("❌ TOKEN KOSONG - User belum login")
                    _isLoading.value = false
                    return@launch
                }

                val itineraries = networkApiManager.getItineraries(token)
                println("📦 ITINERARY SIZE: ${itineraries.size}")

                val itinerariesWithDest = mutableListOf<ItineraryWithDestination>()

                for (itineraryResponse in itineraries) {
                    for (item in itineraryResponse.items ?: emptyList()) {
                        val destination = networkApiManager.getDestinationById(item.destinationId)
                        if (destination != null) {
                            val itinerary = Itinerary(
                                id = itineraryResponse.id.toString(),
                                userId = itineraryResponse.userId.toString(),
                                destinationId = destination.id,
                                date = itineraryResponse.startDate
                            )
                            itinerariesWithDest.add(ItineraryWithDestination(itinerary, destination))
                        }
                    }
                }

                _allItineraries.value = itinerariesWithDest
                groupItineraries()
                filterItineraries()
            } catch (e: Exception) {
                _error.value = e.message
                println("❌ ERROR loadItineraries: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToItinerary(destinationId: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = userSession.token.firstOrNull()
                if (token.isNullOrBlank()) {
                    println("❌ ADD ITINERARY - TOKEN KOSONG")
                    return@launch
                }

                // Step 1: Buat itinerary baru (header)
                val itinerary = networkApiManager.createItinerary(
                    token = token,
                    title = "Perjalanan saya",
                    startDate = date
                )
                if (itinerary == null) {
                    println("❌ Gagal membuat itinerary")
                    return@launch
                }

                println("✅ Itinerary dibuat dengan ID: ${itinerary.id}")

                // Step 2: Tambahkan destinasi sebagai item ke itinerary yang baru dibuat
                val itemAdded = networkApiManager.addItineraryItem(
                    token = token,
                    itineraryId = itinerary.id,
                    destinationId = destinationId.toLong(),
                    day = 1,
                    sequenceOrder = 1,
                    startTime = "08:00",
                    endTime = "10:00"
                )

                if (itemAdded) {
                    println("✅ Item ditambahkan ke itinerary")
                    loadItineraries()
                } else {
                    println("❌ Gagal menambahkan item ke itinerary")
                    _error.value = "Gagal menambahkan destinasi ke itinerary"
                }
            } catch (e: Exception) {
                _error.value = e.message
                println("❌ ERROR addToItinerary: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun removeFromItinerary(itineraryId: String, _date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = userSession.token.firstOrNull()
                if (token.isNullOrBlank()) {
                    println("❌ REMOVE ITINERARY - TOKEN KOSONG")
                    return@launch
                }

                val success = networkApiManager.deleteItinerary(token, itineraryId.toLong())
                if (success) {
                    loadItineraries()
                }
            } catch (e: Exception) {
                _error.value = e.message
                println("❌ ERROR removeFromItinerary: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun groupItineraries() {
        _groupedItinerary.value = _allItineraries.value
            .groupBy { it.itinerary.date }
            .toSortedMap()
    }

    private fun filterItineraries() {
        val query = _searchQuery.value.lowercase().trim()
        if (query.isEmpty()) {
            groupItineraries()
        } else {
            val filtered = _allItineraries.value.filter {
                it.destination.name.lowercase().contains(query) ||
                        it.destination.location.lowercase().contains(query)
            }
            _groupedItinerary.value = filtered
                .groupBy { it.itinerary.date }
                .toSortedMap()
        }
    }

    fun refresh() {
        loadItineraries()
    }
}