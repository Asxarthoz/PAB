package com.example.tourtest.feature.wishlist.presentation

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
import com.example.tourtest.feature.homepage.presentation.DestinationCard
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.wishlist.viewmodel.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishListScreen(
    viewModel: WishlistViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val wishlistDestinations by viewModel.wishlistDestinations.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadWishlist()
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Wishlist Menu", fontWeight = FontWeight.Bold)
                },
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
            wishlistDestinations.isEmpty() -> {
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
                            text = "Tidak ada destinasi favorit",
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
                    items(wishlistDestinations) { destination ->
                        val currentDestinationId = destination.id
                        val isFavorite = viewModel.isFavorite(currentDestinationId)

                        DestinationCard(
                            destination = destination,
                            isWishlisted = isFavorite,
                            isItineraried = false,
                            onWishListClick = {
                                if (isFavorite) {
                                    viewModel.removeFromWishlist(currentDestinationId)
                                } else {
                                    viewModel.addToWishlist(currentDestinationId)
                                }
                            },
                            onItineraryClick = { selectedDate ->
                                val currentUserId = AuthManager.getCurrentUserId() ?: ""
                                ItineraryManager.addDestination(
                                    context = context,
                                    currentUserId = currentUserId,
                                    destinationId = currentDestinationId,
                                    date = selectedDate
                                )
                            },
                            onClick = { onNavigateToDetail(currentDestinationId) }
                        )
                    }
                }
            }
        }
    }
}