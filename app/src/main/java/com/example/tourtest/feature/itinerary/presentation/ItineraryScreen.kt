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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.homepage.presentation.DestinationCard
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.wishlist.manager.WishlistManager
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

    LaunchedEffect(currentUserId) {
        wishListIds = WishlistManager.getAllWish(context)
            .filter { it.userId == currentUserId }
            .map { it.destinationId }.toSet()

        itineraries = ItineraryManager.getItineraryByUser(context, currentUserId)
    }

    val listState = rememberLazyListState()

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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (itineraries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
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
                            text = "Tidak ada destinasi ditemukan",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
                                        text = "📅 Rencana: ${item.date}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

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
                                        ItineraryManager.removeDestination(context, item.id)
                                        itineraries = itineraries.filter { it.id != item.id }
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