package com.example.tourtest.feature.itinerary.presentation
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.core.components.TourizmeSimpleHeader
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.itinerary.viewmodel.ItineraryViewModel
import com.example.tourtest.model.Itinerary

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

    var wishListIds by remember { mutableStateOf(setOf<String>()) }
//    var itineraries by remember { mutableStateOf(listOf<Itinerary>()) }

    // State Alert
    var showDeleteItineraryDialog by remember { mutableStateOf(false) }
    var selectedItineraryId by remember { mutableStateOf<String?>(null) }

    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUserId) {
        wishListIds = FavoriteManager.getAllFavorite(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()

//        itineraries = ItineraryManager.getItineraryByUser(context, currentUserId)
    }

    val listState = rememberLazyListState()

    TourizmeDeleteDialog(
        show = showDeleteItineraryDialog,
        message = "Yakin hapus destinasi dari daftar rencana?",
        onConfirm = {
            selectedItineraryId?.let { id ->
                ItineraryManager.removeDestination(context, id)
//                itineraries = itineraries.filter { it.id != id }
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
                wishListIds = wishListIds - id
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
//                    Box(
//                        modifier = Modifier.fillMaxSize().padding(paddingValues),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize().padding(paddingValues),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
//                    }
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
                            item {
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
                            }

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
                                                text = "Tanggal: $date}",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                ),
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
                                                            wishListIds =
                                                                wishListIds + destination.id
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
//                                            if (currentUserId.isBlank()) {
//                                                Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
//                                            } else {
//                                                val success = ItineraryManager.addDestination(context, currentUserId, destination.id, selectedDate)
//                                                if (success) {
//                                                    itineraries = ItineraryManager.getItineraryByUser(context, currentUserId)
//                                                    Toast.makeText(context, "Berhasil disimpan ke daftar rencana", android.widget.Toast.LENGTH_SHORT).show()
//                                                } else {
//                                                    Toast.makeText(context, "Gagal disimpan ke daftar rencana", android.widget.Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
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