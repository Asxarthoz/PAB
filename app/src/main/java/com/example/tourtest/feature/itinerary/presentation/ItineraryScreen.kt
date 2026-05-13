package com.example.tourtest.feature.itinerary.presentation
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.core.components.TourizmeSimpleHeader
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.itinerary.viewmodel.ItineraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    viewModel: ItineraryViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val allDestinations = remember { HomepageManager.readDestinationsFromData(context) }
    val currentUserId = AuthManager.getCurrentUserId()?: ""

    val groupedItinerary by viewModel.groupedItinerary.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var favoriteIds by remember { mutableStateOf(setOf<String>()) }
    var itinerariedIds by remember { mutableStateOf(setOf<String>()) }

    // State Alert
    var showDeleteItineraryDialog by remember { mutableStateOf(false) }
    var selectedItineraryId by remember { mutableStateOf<String?>(null) }

    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

    fun refreshStatus() {
        favoriteIds = FavoriteManager.getAllFavorite(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()

        itinerariedIds = ItineraryManager.getAllItinerary(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()
    }

    LaunchedEffect(currentUserId) {
        refreshStatus()
        viewModel.loadItineraries()
    }

    val listState = rememberLazyListState()

    TourizmeDeleteDialog(
        show = showDeleteItineraryDialog,
        message = "Yakin hapus destinasi dari daftar rencana?",
        onConfirm = {
            selectedItineraryId?.let { id ->
                viewModel.loadItineraries()
                refreshStatus()
                Toast.makeText(context, "Berhasil dihapus dari rencana", android.widget.Toast.LENGTH_SHORT).show()
            }
            showDeleteItineraryDialog = false
            selectedItineraryId = null
        }, onDismiss = {
            showDeleteItineraryDialog = false
            selectedItineraryId = null
        }
    )

    TourizmeDeleteDialog(
        show = showDeleteFavoriteDialog,
        message = "Hapus dari daftar favorit?",
        onConfirm = {
            selectedDestinationId?.let { id ->
                FavoriteManager.removeDestination(context, currentUserId, id)
                favoriteIds = favoriteIds - id
                Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
            }
            showDeleteFavoriteDialog = false
        },
        onDismiss = { showDeleteFavoriteDialog = false }
    )

    Scaffold(
        topBar = {
            TourizmeSimpleHeader("Rencana Perjalanan")
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        "Error: $error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                groupedItinerary.isEmpty() -> {
                    TourizmeEmptyState("Tidak ada destinasi ditemukan", null)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        groupedItinerary.forEach { (date, items) ->
                            items(items) { itineraryItem ->
                                val destination =
                                    allDestinations.find { it.id == itineraryItem.destination.id }

                                if (destination != null) {
                                    Column {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.small,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Text(
                                                text = "Tanggal: $date",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                ),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        val isFavorite = favoriteIds.contains(destination.id)
                                        DestinationCard(
                                            destination = destination,
                                            isWishlisted = isFavorite,
                                            isItineraried = true,
                                            onWishListClick = {
                                                if (currentUserId.isBlank()) {
                                                    Toast.makeText(
                                                        context,
                                                        "Gagal: User ID tidak ditemukan!",
                                                        android.widget.Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    if (isFavorite) {
                                                        selectedDestinationId = destination.id
                                                        showDeleteFavoriteDialog = true
                                                    } else {
                                                        val success =
                                                            FavoriteManager.addDestination(
                                                                context,
                                                                currentUserId,
                                                                destination.id
                                                            )
                                                        if (success) {
                                                            favoriteIds =
                                                                favoriteIds + destination.id
                                                            Toast.makeText(
                                                                context,
                                                                "Berhasil disimpan ke daftar favorit",
                                                                android.widget.Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Gagal disimpan ke daftar favorit",
                                                                android.widget.Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            },
                                            onItineraryClick = { selectedDate ->
                                            if (currentUserId.isBlank()) {
                                                Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                val success = ItineraryManager.addDestination(context, currentUserId, destination.id, selectedDate)
                                                if (success) {
                                                    viewModel.loadItineraries()
                                                    Toast.makeText(context, "Berhasil disimpan ke daftar rencana", android.widget.Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Gagal disimpan ke daftar rencana", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            },
                                            onClick = { onNavigateToDetail(destination.id) }
                                        )

                                        TextButton(
                                            onClick = {
                                                selectedItineraryId = itineraryItem.destination.id
                                                showDeleteItineraryDialog = true
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text(
                                                "Hapus Rencana",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                                    }
                                }
                            }


                        }
                    }
                }
            }
        }
    }
}