package com.example.tourtest.feature.homepage.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tourtest.R
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.model.Destination
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.wishlist.manager.WishlistManager
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    onNavigateToProfile: () -> Unit,
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

    LaunchedEffect(currentUserId) {
        val myWishlist = WishlistManager.getAllWish(context).filter { it.userId == currentUserId }.map { it.destinationId }.toSet()
        wishListIds = myWishlist

        val myItineraryList = ItineraryManager.getAllItinerary(context).filter { it.userId == currentUserId }.map { it.destinationId }.toSet()
        itineraryListIds = myItineraryList
    }

    val listState = rememberLazyListState()

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
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
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
                        Text(
                            text = "Coba dengan kata kunci lain",
                            fontSize = 14.sp,
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
                    items(filteredDestinations) { destination ->
                        val isFavorite = wishListIds.contains(destination.id)
                        val isItineraried = itineraryListIds.contains(destination.id)
                        DestinationCard(
                            destination = destination,
                            isWishlisted = isFavorite,
                            isItineraried = isItineraried,
                            onWishListClick = {
                                if (isFavorite) {
                                    WishlistManager.removeDestination(context, currentUserId, destination.id)
                                    wishListIds = wishListIds - destination.id
                                } else {
                                    WishlistManager.addDestination(context, currentUserId, destination.id)
                                    wishListIds = wishListIds + destination.id
                                }
                            },
                            onItineraryClick = { selectedDate ->
                                if (currentUserId.isBlank()) {
                                    android.widget.Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    val success = ItineraryManager.addDestination(context, currentUserId, destination.id, selectedDate)
                                    if (success) {
                                        itineraryListIds = itineraryListIds + destination.id
                                        android.widget.Toast.makeText(context, "Berhasil simpan ke jadwal!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "Gagal simpan ke file!", android.widget.Toast.LENGTH_SHORT).show()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationCard(
    destination: Destination,
    isWishlisted: Boolean,
    isItineraried: Boolean,
    onWishListClick: () -> Unit,
    onItineraryClick: (String) -> Unit,
    onClick:() -> Unit
) {
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)

                val startOfToday = calendar.timeInMillis


                return utcTimeMillis >= startOfToday
            }
        }
    )

    if (showBottomSheet) {
       ModalBottomSheet(
           onDismissRequest = { showBottomSheet = false },
           sheetState = sheetState,
           containerColor = MaterialTheme.colorScheme.surface
       ) {
           Column(
               modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
               horizontalAlignment = Alignment.CenterHorizontally
           ) {
               Text(
                   text = "Pilih tanggal rencana",
                   style = MaterialTheme.typography.titleLarge,
                   modifier = Modifier.padding(vertical = 16.dp)
               )

               DatePicker(state = datePickerState, showModeToggle = false)

               Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.End
               ) {
                   TextButton(onClick = { showBottomSheet = false }) {
                       Text(text = "Batal")
                   }
                   Button(onClick = {
                       val selectedDate = datePickerState.selectedDateMillis
                       if (selectedDate != null) {
                           val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
                           onItineraryClick(formattedDate)
                       }
                       showBottomSheet = false
                   }) {
                       Text(text = "Pilih")
                   }
               }
           }
       }
    }

    val openMaps = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination.gmapUrl))
        context.startActivity(intent)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box {
                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.undraw_loading_ui_egb4),
                    error = painterResource(R.drawable.undraw_loading_ui_egb4)
                )

                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onWishListClick,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color.Red else Color.White
                        )
                    }

                    IconButton(
                        onClick = { showBottomSheet = true },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Itinerary",
                            tint = if (isItineraried) Color.Cyan else Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = destination.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = openMaps,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = destination.location,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "Mulai dari ${destination.price}",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
            }
        }
    }
}