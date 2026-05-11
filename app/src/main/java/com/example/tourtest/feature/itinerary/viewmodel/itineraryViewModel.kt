package com.example.tourtest.feature.itinerary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.model.Destination
import com.example.tourtest.model.Itinerary
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItineraryWithDestination(
    val itinerary: Itinerary,
    val destination: Destination
)

class ItineraryViewModel(
    application: Application,
    private val itineraryManager: ItineraryManager,
    private val homepageManager: HomepageManager
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val _groupedItinerary = MutableStateFlow<Map<String, List<ItineraryWithDestination>>>(emptyMap())
    val groupedItinerary: StateFlow<Map<String, List<ItineraryWithDestination>>> = _groupedItinerary.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    fun loadItinerary() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                android.util.Log.d("ITINERARY_DEBUG", "currentUserId: $currentUserId")

                val itineraries = itineraryManager.getItineraryByUser(context, currentUserId)
                val allDestinations = homepageManager.readDestinationsFromData(context)
                val destinationMap = allDestinations.associateBy { it.id }

                val withDestinations = itineraries.mapNotNull { itinerary ->
                    destinationMap[itinerary.destinationId]?.let { destination ->
                        ItineraryWithDestination(itinerary, destination)
                    }
                }

                val grouped = withDestinations.groupBy { it.itinerary.date }
                _groupedItinerary.value = grouped

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromItinerary(itineraryId: String, date: String) {
        viewModelScope.launch {
            val success = itineraryManager.removeDestination(context, itineraryId)
            if (success) {
                val currentGroup = _groupedItinerary.value.toMutableMap()
                val updatedList = currentGroup[date]?.filter { it.itinerary.id != itineraryId } ?: emptyList()
                if (updatedList.isEmpty()) {
                    currentGroup.remove(date)
                } else {
                    currentGroup[date] = updatedList
                }
                _groupedItinerary.value = currentGroup
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}