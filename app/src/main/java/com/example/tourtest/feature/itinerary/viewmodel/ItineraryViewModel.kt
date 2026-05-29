package com.example.tourtest.feature.itinerary.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.model.ItineraryWithDestination
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import android.util.Log
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val itineraryManager: ItineraryManager,
    private val homepageManager: HomepageManager,
    @Named("ItineraryPrefs") private val sharedPrefs: SharedPreferences,
    private val userSession: UserSession
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allItineraryList = MutableStateFlow<List<ItineraryWithDestination>>(emptyList())
    val groupedItinerary: StateFlow<Map<String, List<ItineraryWithDestination>>> = combine(
        _allItineraryList,
        _searchQuery
    ) { list, query ->
        val filtered = if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.destination.name.contains(query, ignoreCase = true) ||
                        it.destination.location.contains(query, ignoreCase = true)
            }
        }
        filtered.groupBy { it.itinerary.date }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val savedQuery = sharedPrefs.getString("itin_search_prefs", "") ?: ""
        _searchQuery.value = savedQuery
        loadItineraries()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        sharedPrefs.edit().putString("itin_search_prefs", query).apply()
    }

    fun loadItineraries() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null

            try {
                val currentUserId = userSession.userId.firstOrNull() ?: ""
                Log.d("ITINERARY_DEBUG", "currentUserId: $currentUserId")

                val itineraries = itineraryManager.getItineraryByUser(context, currentUserId)
                val allDestinations = homepageManager.readDestinationsFromData(context)
                val destinationMap = allDestinations.associateBy { it.id }

                val withDestinations = itineraries.mapNotNull { itinerary ->
                    destinationMap[itinerary.destinationId]?.let { destination ->
                        ItineraryWithDestination(itinerary, destination)
                    }
                }

                _allItineraryList.value = withDestinations

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromItinerary(itineraryId: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = itineraryManager.removeDestination(context, itineraryId)
                if (success) {
                    _allItineraryList.value = _allItineraryList.value.filter { it.itinerary.id != itineraryId }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun refreshItinerary() {
        loadItineraries()
    }
}