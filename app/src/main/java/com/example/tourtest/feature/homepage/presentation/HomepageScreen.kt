package com.example.tourtest.feature.homepage.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
//    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val allDestinations = remember { HomepageManager.readDestinationsFromData(context) }

    val currentUserId = AuthManager.getCurrentUserId()?: ""
    var wishListIds by remember { mutableStateOf(setOf<String>()) }
    var itineraryListIds by remember { mutableStateOf(setOf<String>()) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val filteredDestinations = remember(searchQuery, allDestinations) {
        if (searchQuery.isBlank()) {
            allDestinations
        } else {
            allDestinations.filter { destination ->
                destination.name.contains(searchQuery, ignoreCase = true) || destination.location.contains(searchQuery, ignoreCase = true)
                        || destination.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUserId) {
        wishListIds = FavoriteManager.getAllFavorite(context).filter { it.userId == currentUserId }.map { it.destinationId }.toSet()

        itineraryListIds = ItineraryManager.getAllItinerary(context).filter { it.userId == currentUserId }.map { it.destinationId }.toSet()
    }

    val listState = rememberLazyListState()

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
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Cari destinasi...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Hapus")
                                    }
                                }
                            }
                        )
                    } else {
                        Text("Tourizme", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (isSearchActive) "Tutup pencarian" else "Cari"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!isSearchActive && searchQuery.isBlank()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Temukan Petualanganmu",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Temukan destinasi wisata terbaik di Solo Raya",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isSearchActive || searchQuery.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ditemukan ${filteredDestinations.size} destinasi",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (searchQuery.isNotBlank()) {
                        TextButton(onClick = { searchQuery = "" }) {
                            Text("Reset")
                        }
                    }
                }
            }
            if (filteredDestinations.isEmpty()) {
                TourizmeEmptyState("Tidak ada destinasi ditemukan", "Coba dengan kata kunci lain")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredDestinations) { destination ->
                        val isFavorite = wishListIds.contains(destination.id)
                        val isItineraried = itineraryListIds.contains(destination.id)
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
                                        itineraryListIds = itineraryListIds + destination.id
                                        Toast.makeText(context, "Berhasil disimpan ke daftar rencana!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Gagal disimpan ke daftar rencana!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onClick = {onNavigateToDetail(destination.id)}
                        )
                    }
                }
            }
        }
    }
}