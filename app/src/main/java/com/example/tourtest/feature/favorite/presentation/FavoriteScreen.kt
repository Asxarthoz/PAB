package com.example.tourtest.feature.favorite.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.core.components.TourizmeSimpleHeader
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.favorite.viewmodel.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val currentUserId = AuthManager.getCurrentUserId()?: ""

    val favoriteDestinations by viewModel.favoriteDestinations.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var itinerariedIds by remember { mutableStateOf(setOf<String>()) }

    // State Alert
    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

    fun refreshItineraryStatus() {
        itinerariedIds = ItineraryManager.getAllItinerary(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }
            .toSet()
    }

    LaunchedEffect(currentUserId) {
        viewModel.loadFavorite()
        refreshItineraryStatus()
    }

    val listState = rememberLazyListState()

    TourizmeDeleteDialog(
        show = showDeleteFavoriteDialog,
        message =  "Yakin hapus destinasi dari daftar favorit?",
        onConfirm = {
            selectedDestinationId?.let { id ->
                viewModel.removeFromFavorite(id)
                Toast.makeText(context, "Berhasil dihapus dari favorit", android.widget.Toast.LENGTH_SHORT).show()
            }
            showDeleteFavoriteDialog = false
            selectedDestinationId = null
        },
        onDismiss = {
            showDeleteFavoriteDialog = false
            selectedDestinationId = null
        }
    )

    Scaffold(
        topBar = {
            TourizmeSimpleHeader("Destinasi Favorit")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            else if (favoriteDestinations.isEmpty()) {
                TourizmeEmptyState("Tidak ada destinasi ditemukan", "Coba dengan kata kunci lain!")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteDestinations) { destination ->
                        val isFavorite = favoriteIds.contains(destination.id)
                        val isItineraried = itinerariedIds.contains(destination.id)
                        DestinationCard(
                            destination = destination,
                            isWishlisted = isFavorite,
                            isItineraried = isItineraried,
                            onWishListClick = {
                                if (currentUserId.isBlank()) {
                                    Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    if (isFavorite) {
                                        selectedDestinationId = destination.id
                                        showDeleteFavoriteDialog = true
                                    } else {
                                        viewModel.addToFavorite(destination.id)
                                        Toast.makeText(context, "Disimpan ke favorit", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },onItineraryClick = { selectedDate ->
                                if (currentUserId.isBlank()) {
                                    Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    val success = ItineraryManager.addDestination(context, currentUserId, destination.id, selectedDate)
                                    if (success) {
                                        refreshItineraryStatus()
                                        Toast.makeText(context, "Berhasil disimpan ke daftar rencana", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Gagal disimpan ke daftar rencana", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onClick = { onNavigateToDetail(destination.id) }
                        )
                    }
                }
            }
        }
    }
}
