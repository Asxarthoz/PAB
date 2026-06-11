package com.example.tourtest.feature.itinerary.presentation

import TourizmeDatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.core.components.DestinationCard
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.core.components.TourizmeSimpleHeader
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.core.network.NetworkItineraryViewModel
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.model.Destination
import com.example.tourtest.model.ItineraryWithDestination
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBlueMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryContent(
    searchQuery: String,
    groupedItinerary: Map<String, List<ItineraryWithDestination>>,
    favoriteIds: Set<String>,
    isLoading: Boolean,
    error: String?,
    listState: LazyListState,
    onSearchQueryChange: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onWishListClick: (Destination) -> Unit,
    onClick: (Destination) -> Unit,
    onDeleteItineraryClick: (ItineraryWithDestination) -> Unit,
    onItineraryClick: (Destination) -> Unit
) {
    Scaffold(
        topBar = {
            TourizmeSimpleHeader(
                title = "Rencana Perjalanan",
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onNotificationClick = onNotificationClick
            )
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
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = "Tanggal: $date",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            items(items) { item ->
                                Column {
                                    DestinationCard(
                                        destination = item.destination,
                                        isWishlisted = favoriteIds.contains(item.destination.id),
                                        isItineraried = true,
                                        onWishListClick = { onWishListClick(item.destination) },
                                        onItineraryClick = { onItineraryClick(item.destination) },
                                        onClick = { onNavigateToDetail(item.destination.id) }
                                    )

                                    TextButton(
                                        onClick = { onDeleteItineraryClick(item) },
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryGuestContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold(
        topBar = {
            TourizmeSimpleHeader(
                title = "Rencana Perjalanan",
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onNotificationClick = onNotificationClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TourizmeEmptyState(
                message = "Anda belum Login",
                subMessage = "Silahkan Login untuk menambah/melihat rencana perjalanan anda",
                imageVector = Icons.Default.Lock,
                actionButton = {
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TourizmeBlueMain,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Login Sekarang!", fontFamily = MontserratFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    viewModel: NetworkItineraryViewModel,
    userSession: UserSession,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val currentUserIdFromStore by userSession.userId.collectAsState(initial = null)
    val currentUserId = currentUserIdFromStore ?: "GUEST"
    val isGuest = currentUserId == "GUEST" || currentUserId.isBlank()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val groupedItinerary by viewModel.groupedItinerary.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var favoriteIds by remember { mutableStateOf(setOf<String>()) }
    var itinerariedIds by remember { mutableStateOf(setOf<String>()) }

    var showDeleteItineraryDialog by remember { mutableStateOf(false) }
    var selectedItinerary by remember { mutableStateOf<ItineraryWithDestination?>(null) }

    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }
    var selectedDestinationForReschedule by remember { mutableStateOf<Destination?>(null) }

    fun refreshStatus() {
        favoriteIds = FavoriteManager.getAllFavorite(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()

        itinerariedIds = ItineraryManager.getAllItinerary(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()
    }

    LaunchedEffect(currentUserId) {
        if (!isGuest) {
            refreshStatus()
            viewModel.loadItineraries()
        }
    }

    selectedDestinationForReschedule?.let { destination ->
        TourizmeDatePicker(
            destination = destination,
            onDismiss = { selectedDestinationForReschedule = null },
            onDateSelected = { formattedDate, targetTimeMillis ->
                if (currentUserId.isNotBlank()) {
                    selectedDestinationForReschedule?.let { dest ->
                        val success = ItineraryManager.addDestination(context, currentUserId, dest.id, formattedDate)
                        if (success) {
                            viewModel.loadItineraries()
                            refreshStatus()
                            Toast.makeText(context, "Jadwal rencana berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
    }

    val listState = rememberLazyListState()

    TourizmeDeleteDialog(
        show = showDeleteItineraryDialog,
        message = "Yakin hapus destinasi dari daftar rencana?",
        onConfirm = {
            selectedItinerary?.let { item ->
                // ✅ PERBAIKAN: gunakan parameter yang benar
                viewModel.removeFromItinerary(
                    itineraryId = item.itinerary.id,
                    _date = item.itinerary.date
                )
                refreshStatus()
                Toast.makeText(context, "Berhasil dihapus dari rencana", Toast.LENGTH_SHORT).show()
            }
            showDeleteItineraryDialog = false
            selectedItinerary = null
        },
        onDismiss = {
            showDeleteItineraryDialog = false
            selectedItinerary = null
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

    if (currentUserId == "GUEST" || currentUserId.isBlank()) {
        ItineraryGuestContent(
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onNotificationClick = onNavigateToNotification,
            onNavigateToLogin = onNavigateToLogin
        )
    } else {
        ItineraryContent(
            searchQuery = searchQuery,
            groupedItinerary = groupedItinerary,
            favoriteIds = favoriteIds,
            isLoading = isLoading,
            error = error,
            listState = listState,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onNotificationClick = onNavigateToNotification,
            onNavigateToDetail = onNavigateToDetail,
            onWishListClick = { destination ->
                if (currentUserId.isBlank()) {
                    Toast.makeText(context, "Gagal: User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
                } else {
                    if (favoriteIds.contains(destination.id)) {
                        selectedDestinationId = destination.id
                        showDeleteFavoriteDialog = true
                    } else {
                        FavoriteManager.addDestination(context, currentUserId, destination.id)
                        favoriteIds = favoriteIds + destination.id
                        Toast.makeText(context, "Disimpan ke favorit", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onClick = { destination -> onNavigateToDetail(destination.id) },
            onDeleteItineraryClick = { item ->
                selectedItinerary = item
                showDeleteItineraryDialog = true
            },
            onItineraryClick = { destination -> selectedDestinationForReschedule = destination }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ItineraryPreview() {
    MaterialTheme {
        ItineraryContent(
            searchQuery = "",
            groupedItinerary = emptyMap(),
            favoriteIds = setOf(),
            isLoading = false,
            error = null,
            listState = rememberLazyListState(),
            onSearchQueryChange = {},
            onNotificationClick = {},
            onNavigateToDetail = { id -> },                // 👈 Diberi penampung String ID
            onWishListClick = { destination -> },          // 👈 Diberi penampung Destination
            onClick = { destination -> },                  // 👈 Diberi penampung Destination
            onDeleteItineraryClick = { itinerary -> },    // 👈 Diberi penampung ItineraryWithDestination
            onItineraryClick = { destination -> }          // 👈 Diberi penampung Destination
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ItineraryGuestPreview() {
    MaterialTheme {
        ItineraryGuestContent(
            searchQuery = "",
            onSearchQueryChange = {},
            onNotificationClick = {},
            onNavigateToLogin = {}
        )
    }
}