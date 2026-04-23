// feature/homepage/presentation/HomepageScreen.kt
package com.example.tourtest.feature.homepage.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tourtest.R
import com.example.tourtest.model.Destination
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val allDestinations = remember { HomepageManager.readDestinationsFromData(context) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val filteredDestinations = remember(searchQuery, allDestinations) {
        if (searchQuery.isBlank()) {
            allDestinations
        } else {
            allDestinations.filter { destination ->
                destination.name.contains(searchQuery, ignoreCase = true) ||
                        destination.location.contains(searchQuery, ignoreCase = true)
            }
        }
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
                    // Navigasi ke Profile (Type-Safe)
                    IconButton(onClick = {
                        navController.navigate(Screen.Profile)
                    }) {
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
            // Header
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

            // Info hasil pencarian
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

            // List Destinasi
            if (filteredDestinations.isEmpty()) {
                // Tampilan kosong
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
                        DestinationCard(destination = destination)
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationCard(
    destination: Destination
) {
    val context = LocalContext.current

    val openMaps = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination.gmapUrl))
        context.startActivity(intent)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Gambar Destinasi
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

            // Informasi Destinasi
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nama Destinasi
                Text(
                    text = destination.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Tombol Lokasi (Maps)
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

                // Harga
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