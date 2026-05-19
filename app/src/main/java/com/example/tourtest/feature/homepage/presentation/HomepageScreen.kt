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
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.homepage.viewmodel.HomepageViewModel
import com.example.tourtest.model.Destination
import com.example.tourtest.provider.homepage.DestinationProvider
import com.example.tourtest.ui.theme.TourizmeTheme


@Composable
fun HomepageContent(
    greeting: String,
    userName: String,
    searchQuery: String,
    isSearchActive: Boolean,
    listState: LazyListState,
//    currentUserId: String,
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
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
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
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = userName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        IconButton(onClick = onNavigateToNotification) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(0.dp))

                    TextField(
                        value = searchQuery,
                        onValueChange =  onSearchQueryChange ,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp).heightIn(min = 44.dp),
                        placeholder = { Text("Mau liburan ke mana nih?") },
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
                                IconButton(onClick =  onClearSearch ) {
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
                        TextButton(onClick = { onClearSearch })
                        {
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
    viewModel: HomepageViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotification: () -> Unit
) {
    val context = LocalContext.current
    val currentUserId = AuthManager.getCurrentUserId()?: ""
    val listState = rememberLazyListState()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredDestinations by viewModel.filteredDestinations.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle() // Pakai yang ini
//    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val greeting = remember { viewModel.getGreeting() }
    val userName = remember { AuthManager.getUserById(context, currentUserId)?.nickName ?: "Traveler" }

    var favoriteIds by remember { mutableStateOf(setOf<String>()) }
    var itinerariedIds by remember { mutableStateOf(setOf<String>()) }
    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

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
    }

    TourizmeDeleteDialog(
        show = showDeleteFavoriteDialog,
        message =  "Yakin hapus destinasi dari daftar favorit?",
        onConfirm = {
            selectedDestinationId?.let { id ->
                val succes = FavoriteManager.removeDestination(context, currentUserId, id)
                if (succes) {
                    favoriteIds = favoriteIds - id
                    Toast.makeText(context, "Berhasil dihapus dari favorit", android.widget.Toast.LENGTH_SHORT).show()
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

    TourizmeDatePicker(
        destination = selectedDestinationForItinerary,
        onDismiss = { selectedDestinationForItinerary = null },
        onDateSelected = { formattedDate, targetTimeMillis ->
            if (currentUserId.isBlank()) {
                Toast.makeText(context, "Gagal: User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            } else {
                selectedDestinationForItinerary?.let { destination ->
                    val success = ItineraryManager.addDestination(context, currentUserId, destination.id, formattedDate)
                    if (success) {
                        itinerariedIds = itinerariedIds + destination.id
                        Toast.makeText(context, "Berhasil disimpan ke daftar rencana!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Gagal disimpan ke daftar rencana!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

    HomepageContent(
        greeting = greeting,
        userName = userName,
        searchQuery = searchQuery,
        isSearchActive = isSearchActive,
        listState = listState,
//        currentUserId = currentUserId,
        filteredDestinations = filteredDestinations,
        favoriteIds = favoriteIds,
        itinerariedIds = itinerariedIds,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onClearSearch = { viewModel.clearSearch() },
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToNotification = onNavigateToNotification,
        onWishListClick = { destination ->
            if (currentUserId.isBlank()) {
                Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                if (favoriteIds.contains(destination.id)) {
                    selectedDestinationId = destination.id
                    showDeleteFavoriteDialog = true
                } else {
                    val success = FavoriteManager.addDestination(context, currentUserId, destination.id)
                    if (success) {
                        favoriteIds = favoriteIds + destination.id
                        Toast.makeText(context, "Berhasil disimpan ke daftar favorit", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Gagal disimpan ke daftar favorit", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        onItineraryClick = { destination ->
            selectedDestinationForItinerary = destination
        },
        onClick = { destination -> onNavigateToDetail(destination.id) }
    )
}

@Preview(showSystemUi = true)
@Composable
fun HomepagePreview(@PreviewParameter(DestinationProvider::class) destinations: List<Destination>) {
    TourizmeTheme {
        HomepageContent(
            greeting = "Selamat Pagi",
            userName = "Tian",
            searchQuery = "",
            isSearchActive = false,
            listState = rememberLazyListState(),
//            currentUserId = "123",
            filteredDestinations = destinations,
            favoriteIds = setOf(),
            itinerariedIds = setOf(),
            onSearchQueryChange = {},
            onClearSearch = {},
            onNavigateToDetail = {},
            onNavigateToNotification = {},
            onWishListClick = {},
            onItineraryClick = { _ -> },
            onClick = {}
        )
    }
}
