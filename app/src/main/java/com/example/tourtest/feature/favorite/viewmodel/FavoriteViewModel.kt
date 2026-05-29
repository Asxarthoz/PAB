package com.example.tourtest.feature.favorite.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteManager: FavoriteManager,
    private val homepageManager: HomepageManager,
    @Named("FavoritePrefs") private val sharedPrefs: SharedPreferences,
    private val userSession: UserSession
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allFavorites = MutableStateFlow<List<Destination>>(emptyList())

    val favoriteDestinations: StateFlow<List<Destination>> = combine(
        _allFavorites,
        _searchQuery
    ) { destinations, query ->
        if (query.isBlank()) {
            destinations
        } else {
            destinations.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        sharedPrefs.edit().putString("fav_search_prefs", query).apply()
    }

    init {
        val savedQuery = sharedPrefs.getString("fav_search_prefs", "") ?: ""
        _searchQuery.value = savedQuery
        loadFavorite()
    }

    fun loadFavorite() {
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            _error.value = null

            try {
                val currentUserIdFromStore = userSession.userId.firstOrNull()
                val currentUserId = currentUserIdFromStore ?: ""
                Log.d("FAVORITE_DEBUG", "currentUserId: $currentUserId")

                val favoritesData = favoriteManager.getAllFavorite(context)
                val favoriteIds = favoritesData.filter { it.userId == currentUserId }.map { it.destinationId }.toSet()
                _favoriteIds.value = favoriteIds

                val allDestinations = homepageManager.readDestinationsFromData(context)
                val favorites = allDestinations.filter { it.id in favoriteIds }
                _allFavorites.value = favorites

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorite(destinationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUserIdFromStore = userSession.userId.firstOrNull()
                val currentUserId = currentUserIdFromStore ?: ""
                val success = favoriteManager.addDestination(context, currentUserId, destinationId)
                if (success) {
                    _favoriteIds.value = _favoriteIds.value + destinationId
                    val allDestinations = homepageManager.readDestinationsFromData(context)
                    val newDestination = allDestinations.find { it.id == destinationId }
                    newDestination?.let {
                        _allFavorites.value = _allFavorites.value + it
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeFromFavorite(destinationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUserIdFromStore = userSession.userId.firstOrNull()
                val currentUserId = currentUserIdFromStore ?: ""
                val success = favoriteManager.removeDestination(context, currentUserId, destinationId)
                if (success) {
                    _favoriteIds.value = _favoriteIds.value - destinationId
                    _allFavorites.value = _allFavorites.value.filter { it.id != destinationId }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun isFavorite(destinationId: String): Boolean {
        return destinationId in _favoriteIds.value
    }

    fun clearError() {
        _error.value = null
    }
}