package com.example.tourtest.feature.homepage.presentation

import TourizmeDatePicker
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.core.network.NetworkHomepageViewModel
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.model.Destination
import com.example.tourtest.provider.homepage.DestinationProvider
import com.example.tourtest.ui.theme.InterFontFamily
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBlueMain
import com.example.tourtest.ui.theme.TourizmeTheme

@Composable
fun HomepageContent(
    greeting: String,
    userName: String,
    searchQuery: String,
    isSearchActive: Boolean,
    listState: LazyListState,
    filteredDestinations: List<Destination>,
    favoriteIds: Set<String>,
    itinerariedIds: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotification: () -> Unit,
    onWishListClick: (Destination) -> Unit,
    onItineraryClick: (Destination) -> Unit,
    onClick: (Destination) -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(
                color = TourizmeBlueMain,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Text(
                                text = "$greeting, ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = MontserratFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp,
                                color = Color.White
                            )

                            Text(
                                text = userName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = MontserratFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }

                        IconButton(onClick = onNavigateToNotification) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = Color.White
                            )
                        }
                    }

                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).heightIn(min = 44.dp),
                        placeholder = { Text("Cari destinasi impian?", fontFamily = InterFontFamily, fontWeight = FontWeight.Normal ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = onClearSearch) {
                                    Icon(Icons.Default.Close, contentDescription = "Hapus")
                                }
                            }
                        }
                    )
                }
            }
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
                        fontFamily = MontserratFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Temukan destinasi wisata terbaik di Solo Raya",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = MontserratFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
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
                        TextButton(onClick = { onClearSearch }) {
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
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredDestinations) { destination ->
                        val isFavorite = favoriteIds.contains(destination.id)
                        val isItineraried = itinerariedIds.contains(destination.id)
                        DestinationCard(
                            destination = destination,
                            isWishlisted = isFavorite,
                            isItineraried = isItineraried,
                            onWishListClick = { onWishListClick(destination) },
                            onItineraryClick = { onItineraryClick(destination) },
                            onClick = { onNavigateToDetail(destination.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    viewModel: NetworkHomepageViewModel,
    userSession: UserSession,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val currentUserIdFromStore by userSession.userId.collectAsState(initial = null)
    val currentUserId = currentUserIdFromStore ?: "GUEST"
    val listState = rememberLazyListState()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredDestinations by viewModel.filteredDestinations.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()

    val greeting = remember { viewModel.getGreeting() }
    val userName = remember(currentUserId) {
        // TODO: Nanti ganti dengan UserSession atau API
        "Traveler"
    }

    var favoriteIds by remember { mutableStateOf(setOf<String>()) }
    var itinerariedIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }
    var showLoginPromptDialog by remember { mutableStateOf(false) }

    var selectedDestinationForItinerary by remember { mutableStateOf<Destination?>(null) }

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
        viewModel.loadDestinations()
    }

    TourizmeDeleteDialog(
        show = showDeleteFavoriteDialog,
        message = "Yakin hapus destinasi dari daftar favorit?",
        onConfirm = {
            selectedDestinationId?.let { id ->
                val success = FavoriteManager.removeDestination(context, currentUserId, id)
                if (success) {
                    favoriteIds = favoriteIds - id
                    Toast.makeText(context, "Berhasil dihapus dari favorit", Toast.LENGTH_SHORT).show()
                }
            }
            showDeleteFavoriteDialog = false
            selectedDestinationId = null
        },
        onDismiss = {
            showDeleteFavoriteDialog = false
            selectedDestinationId = null
        }
    )

    selectedDestinationForItinerary?.let { destination ->
        TourizmeDatePicker(
            destination = destination,
            onDismiss = { selectedDestinationForItinerary = null },
            onDateSelected = { formattedDate, targetTimeMillis ->
                if (currentUserId.isBlank()) {
                    Toast.makeText(context, "Gagal: User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
                } else {
                    selectedDestinationForItinerary?.let { dest ->
                        val success = ItineraryManager.addDestination(context, currentUserId, dest.id, formattedDate)
                        if (success) {
                            itinerariedIds = itinerariedIds + dest.id
                            Toast.makeText(context, "Berhasil disimpan ke daftar rencana!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Gagal disimpan ke daftar rencana!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
    }

    if (showLoginPromptDialog) {
        AlertDialog(
            onDismissRequest = { showLoginPromptDialog = false },
            title = { Text(text = "Fitur Terbatas") },
            text = { Text(text = "Mohon login terlebih dahulu untuk menggunakan fitur ini.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginPromptDialog = false
                        onNavigateToLogin()
                    }
                ) {
                    Text(text = "Login")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLoginPromptDialog = false }
                ) {
                    Text(text = "Batal")
                }
            }
        )
    }

    HomepageContent(
        greeting = greeting,
        userName = userName,
        searchQuery = searchQuery,
        isSearchActive = isSearchActive,
        listState = listState,
        filteredDestinations = filteredDestinations,
        favoriteIds = favoriteIds,
        itinerariedIds = itinerariedIds,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onClearSearch = { viewModel.clearSearch() },
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToNotification = onNavigateToNotification,
        onWishListClick = { destination ->
            if (currentUserId == "GUEST" || currentUserId.isBlank()) {
                showLoginPromptDialog = true
            } else {
                if (favoriteIds.contains(destination.id)) {

                    selectedDestinationId = destination.id
                    showDeleteFavoriteDialog = true
                } else {
                    viewModel.addToWishlist(destination.id)
                    favoriteIds = favoriteIds + destination.id
                    Toast.makeText(context, "Mengirim ke server...", Toast.LENGTH_SHORT).show()
                }
            }
        },
        onItineraryClick = { destination ->
            if (currentUserId == "GUEST" || currentUserId.isBlank()) {
                showLoginPromptDialog = true
            } else {
                selectedDestinationForItinerary = destination
            }
        },
        onClick = { destination -> onNavigateToDetail(destination.id) }
    )
}
@Preview(showSystemUi = true)
@Composable
fun HomepagePreview() { // 👈 Melepas @PreviewParameter yang rusak
    TourizmeTheme {
        HomepageContent(
            greeting = "Selamat Pagi",
            userName = "Tian",
            searchQuery = "",
            isSearchActive = false,
            listState = rememberLazyListState(),
            filteredDestinations = emptyList(), // 👈 Menggunakan list kosong agar aman saat build
            favoriteIds = setOf(),
            itinerariedIds = setOf(),
            onSearchQueryChange = {},
            onClearSearch = {},
            onNavigateToDetail = { id -> },       // 👈 Solusi: Diberi penampung 'id' agar tidak error
            onNavigateToNotification = {},
            onWishListClick = { destination -> }, // 👈 Solusi: Diberi penampung 'destination'
            onItineraryClick = { destination -> },// 👈 Solusi: Diberi penampung 'destination'
            onClick = { destination -> }          // 👈 Solusi: Diberi penampung 'destination'
        )
    }
}