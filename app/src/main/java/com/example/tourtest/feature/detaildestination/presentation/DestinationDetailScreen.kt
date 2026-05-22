package com.example.tourtest.feature.detaildestination.presentation

import TourizmeDatePicker
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.tourtest.R
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.detaildestination.viewmodel.DetailViewModel
import com.example.tourtest.model.Destination
import scheduleNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailContent(
    destination: Destination?,
    isFavorite: Boolean,
    isItineraried: Boolean,
    isLoading: Boolean,
    scrollState: ScrollState,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onOpenMaps: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(destination?.name ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            destination == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Destinasi tidak ditemukan")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = destination.imageUrl,
                        contentDescription = destination.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.undraw_loading_ui_egb4),
                        error = painterResource(R.drawable.undraw_loading_ui_egb4)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onFavoriteClick
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = onCalendarClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Itinerary",
                                tint = if (isItineraried) Color.Cyan else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Text(
                        text = destination.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Mulai dari Rp ${destination.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TextButton(
                        onClick = onOpenMaps,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lokasi",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = destination.location,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = "Deskripsi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = destination.description,
                        textAlign = TextAlign.Justify,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailScreen(
    viewModel: DetailViewModel,
    userSession: UserSession,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val userIdFromStore by userSession.userId.collectAsState(initial = null)
    val currentUserId = userIdFromStore ?: "GUEST"
    val destinationState by viewModel.destination.collectAsStateWithLifecycle()
    val destination = destinationState
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isItineraried by viewModel.isPlanned.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDestinationForItinerary by remember { mutableStateOf<Destination?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val scrollState = rememberScrollState()
    val openMaps = {
        try {
            val gmapUrl = destination?.gmapUrl
            if (!gmapUrl.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gmapUrl))
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "URL lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka peta", Toast.LENGTH_SHORT).show()
        }
    }
    var showLoginPromptDialog by remember { mutableStateOf(false) }

    TourizmeDeleteDialog(
        show = showDeleteDialog,
        message = "Yakin ingin menghapus destinasi ini dari favorit?",
        onConfirm = {
            viewModel.toggleFavorite()
            showDeleteDialog = false
            Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
        },
        onDismiss = { showDeleteDialog = false }
    )

    selectedDestinationForItinerary?.let { currentDestination ->
        TourizmeDatePicker(
            destination = currentDestination,
            onDismiss = { selectedDestinationForItinerary = null },
            onDateSelected = { formattedDate, targetTimeMillis ->
                viewModel.addToItinerary(formattedDate)

                scheduleNotification(
                    context = context,
                    targetTimeMillis = targetTimeMillis,
                    destinationName = currentDestination.name,
                    destinationId = currentDestination.id
                )

                Toast.makeText(context, "Berhasil disimpan ke daftar rencana", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showLoginPromptDialog) {
        AlertDialog(
            onDismissRequest = { showLoginPromptDialog = false },
            title = { Text("Fitur Terbatas") },
            text = { Text("Silahkan login terlebih dahulu untuk menggunakan fitur Favorite dan Itinerary.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginPromptDialog = false
                        onNavigateToLogin()
                    }
                ) {
                    Text("Login")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginPromptDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    DestinationDetailContent(
        destination = destination,
        isFavorite = isFavorite,
        isItineraried = isItineraried,
        isLoading = isLoading,
        scrollState = scrollState,
        onBack = onBack,
        onFavoriteClick = {
            if (currentUserId == "GUEST" || currentUserId.isBlank()) {
                showLoginPromptDialog = true
            } else {
                if (isFavorite) showDeleteDialog = true else viewModel.toggleFavorite()
            }
        },
        onCalendarClick = {
            if (currentUserId == "GUEST" || currentUserId.isBlank()) {
                showLoginPromptDialog = true
            } else {
                showDatePicker = true
            } },
        onOpenMaps = openMaps
    )
}

@Preview(showSystemUi = true)
@Composable
fun DestinationDetailPreview() {
    MaterialTheme {
        DestinationDetailContent(
            destination = Destination(
                id = "1",
                name = "Candi Borobudur",
                location = "Magelang",
                description = "Candi Buddha terbesar di dunia yang dibangun pada abad ke-9.",
                price = "50.000",
                imageUrl = "",
                gmapUrl = "",
            ),
            isFavorite = true,
            isItineraried = false,
            isLoading = false,
            scrollState = rememberScrollState(),
            onBack = {},
            onFavoriteClick = {},
            onCalendarClick = {},
            onOpenMaps = {}
        )
    }
}
