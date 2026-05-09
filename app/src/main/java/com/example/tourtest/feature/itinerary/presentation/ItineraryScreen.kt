package com.example.tourtest.feature.itinerary.presentation
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
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.core.components.TourizmeSimpleHeader
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.model.Itinerary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val allDestinations = remember { HomepageManager.readDestinationsFromData(context) }
    val currentUserId = AuthManager.getCurrentUserId()?: ""

    var wishListIds by remember { mutableStateOf(setOf<String>()) }
    var itineraries by remember { mutableStateOf(listOf<Itinerary>()) }

    // State Alert
    var showDeleteItineraryDialog by remember { mutableStateOf(false) }
    var selectedItineraryId by remember { mutableStateOf<String?>(null) }

    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUserId) {
        wishListIds = FavoriteManager.getAllFavorite(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()

        itineraries = ItineraryManager.getItineraryByUser(context, currentUserId)
    }

    val listState = rememberLazyListState()

    TourizmeDeleteDialog(
        show = showDeleteItineraryDialog,
        message = "Yakin hapus destinasi dari daftar rencana?",
        onConfirm = {
            selectedItineraryId?.let { id ->
                ItineraryManager.removeDestination(context, id)
                itineraries = itineraries.filter { it.id != id }
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
        message =  "Yakin hapus destinasi dari daftar favorit?",
        onConfirm = {
            selectedDestinationId?.let { id ->
                FavoriteManager.removeDestination(context, currentUserId, id)
                wishListIds = wishListIds - id
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
            TourizmeSimpleHeader("Rencana Perjalanan")
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (itineraries.isEmpty()) {
                TourizmeEmptyState("Tidak ada destinasi ditemukan", null)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(itineraries) { item ->
                        val destination = allDestinations.find { it.id == item.destinationId }

                        if (destination != null) {
                            Column {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "Rencana: ${item.date}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val isFavorite = wishListIds.contains(destination.id)
                                DestinationCard(
                                    destination = destination,
                                    isWishlisted = isFavorite,
                                    isItineraried = true,
                                    onWishListClick = {
                                        if (currentUserId.isBlank()) {
                                            Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
                                        } else {
                                            if (isFavorite) {
                                                selectedDestinationId = destination.id
                                                showDeleteFavoriteDialog = true
                                            } else {
                                                val success = FavoriteManager.addDestination(context, currentUserId, destination.id)
                                                if (success) {
                                                    wishListIds = wishListIds + destination.id
                                                    Toast.makeText(context, "Berhasil disimpan ke daftar favorit", android.widget.Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Gagal disimpan ke daftar favorit", android.widget.Toast.LENGTH_SHORT).show()
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
                                                itineraries = ItineraryManager.getItineraryByUser(context, currentUserId)
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
                                        selectedItineraryId = item.id
                                        showDeleteItineraryDialog = true
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Hapus Rencana", color = MaterialTheme.colorScheme.error)
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