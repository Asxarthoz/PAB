package com.example.tourtest.feature.detaildestination.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.tourtest.R
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.feature.detaildestination.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailScreen(
    viewModel: DetailViewModel,
    destinationId: String,
    onBack: () -> Unit,
    onNavigateToNotification: () -> Unit
) {
    val context = LocalContext.current

    val destination by viewModel.destination.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isItineraried by viewModel.isPlanned.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val scrollState = rememberScrollState()

    val openMaps = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination?.gmapUrl))
        context.startActivity(intent)
    }

    TourizmeDeleteDialog(
        show = showDeleteDialog,
        message = "Yakin ingin menghapus destinasi ini dari favorit?",
        onConfirm = {
            viewModel.toggleFavorite() // Logika hapus di ViewModel
            showDeleteDialog = false
            Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
        },
        onDismiss = { showDeleteDialog = false }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        val formattedDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                            .format(java.util.Date(selectedDate))
                        viewModel.addToItinerary(formattedDate)
                        Toast.makeText(context, "Jadwal rencana diperbarui", Toast.LENGTH_SHORT).show()
                    }
                    showDatePicker = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    AsyncImage(
                        model = destination?.imageUrl,
                        contentDescription = destination?.name,
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
                            onClick = {
                                if (isFavorite) {
                                    showDeleteDialog = true
                                } else {
                                    viewModel.toggleFavorite()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = { showDatePicker = true }
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
                        text = destination!!.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Mulai dari Rp ${destination!!.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TextButton(
                        onClick = openMaps,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lokasi",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = destination!!.location,
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
                        text = destination!!.description,
                        textAlign = TextAlign.Justify,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}