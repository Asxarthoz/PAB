package com.example.tourtest.feature.itinerary.presentation

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.homepage.presentation.DestinationCard
import com.example.tourtest.feature.itinerary.viewmodel.ItineraryViewModel
import com.example.tourtest.feature.wishlist.manager.WishlistManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    viewModel: ItineraryViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val allDestinations = remember { HomepageManager.readDestinationsFromData(context) }
    val currentUserId = AuthManager.getCurrentUserId() ?: ""
    val groupedItinerary by viewModel.groupedItinerary.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedItineraryId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadItinerary()
    }

    var wishListIds by remember { mutableStateOf(setOf<String>()) }
    LaunchedEffect(currentUserId) {
        wishListIds = WishlistManager.getAllWish(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()
    }

    val listState = rememberLazyListState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi hapus") },
            text = { Text("Yakin hapus destinasi dari daftar rencana?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedItineraryId?.let { id ->
                            selectedDate?.let { date ->
                                viewModel.removeFromItinerary(id, date)
                            }
                        }
                        showDeleteDialog = false
                        selectedItineraryId = null
                        selectedDate = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    selectedItineraryId = null
                    selectedDate = null
                }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rencana Perjalanan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }
            groupedItinerary.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Tidak ada rencana perjalanan",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = listState,
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedItinerary.forEach { (date, items) ->
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = "Rencana: $date",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        items(items) { itineraryWithDest ->
                            val destination = itineraryWithDest.destination
                            Column {
                                DestinationCard(
                                    destination = destination,
                                    isWishlisted = wishListIds.contains(destination.id),
                                    isItineraried = true,
                                    onWishListClick = {
                                        if (wishListIds.contains(destination.id)) {
                                            WishlistManager.removeDestination(context, currentUserId, destination.id)
                                            wishListIds = wishListIds - destination.id
                                        } else {
                                            WishlistManager.addDestination(context, currentUserId, destination.id)
                                            wishListIds = wishListIds + destination.id
                                        }
                                    },
                                    onItineraryClick = { },
                                    onClick = { onNavigateToDetail(destination.id) }
                                )

                                TextButton(
                                    onClick = {
                                        selectedItineraryId = itineraryWithDest.itinerary.id
                                        selectedDate = date
                                        showDeleteDialog = true
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