package com.example.tourtest.feature.homepage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.model.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomepageViewModel(
    private val getAllDestinations: () -> List<Destination>
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()
    private val _allDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val allDestinations: StateFlow<List<Destination>> = _allDestinations.asStateFlow()
    private val _filteredDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val filteredDestinations: StateFlow<List<Destination>> = _filteredDestinations.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDestinations()
    }

    fun loadDestinations() {
        viewModelScope.launch {
            _isLoading.value = true
            val data = getAllDestinations()
            _allDestinations.value = data
            _filteredDestinations.value = data
            _isLoading.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterDestinations()
    }

    fun toggleSearchActive() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value && _searchQuery.value.isNotBlank()) {
            clearSearch()
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _filteredDestinations.value = _allDestinations.value
    }

    private fun filterDestinations() {
        val query = _searchQuery.value
        if (query.isBlank()) {
            _filteredDestinations.value = _allDestinations.value
        } else {
            _filteredDestinations.value = _allDestinations.value.filter { destination ->
                destination.name.contains(query, ignoreCase = true) ||
                        destination.location.contains(query, ignoreCase = true) ||
                        destination.description.contains(query, ignoreCase = true)
            }
        }
    }
}