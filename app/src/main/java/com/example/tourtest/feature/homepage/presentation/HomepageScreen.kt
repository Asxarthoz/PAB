package com.example.tourtest.feature.homepage.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tourtest.R
import com.example.tourtest.model.Destination
import com.example.tourtest.feature.homepage.manager.HomepageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val destinations = remember { HomepageManager.readDestinationsFromData(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tourizme", fontWeight = FontWeight.Bold)},
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                }
            )
        }
    ) {
        paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Temukan Petualanganmu yang selanjutnya di Solo Raya",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Temukan destinasi wisat terbaik di Solo Raya",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(destinations) { destination ->
                DestinationCard(destination)
            }
        }
    }
}

@Composable
fun DestinationCard(destination: Destination) {
    val context = LocalContext.current

    val openMaps = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination.gmapUrl))
        context.startActivity(intent)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = destination.name,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.undraw_loading_ui_egb4),
                error = painterResource(R.drawable.undraw_loading_ui_egb4)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = destination.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = openMaps, contentPadding = PaddingValues(0.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = destination.location, color = MaterialTheme.colorScheme.secondary)
                }
                Text(text = destination.location, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mulai dari ${destination.price}",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
