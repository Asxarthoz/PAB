package com.example.tourtest.feature.homepage.presentation

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.remember
//import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tourtest.R

data class Destination(
    val name: String,
    val location: String,
    val price: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val destinations = remember { readDestinationsFromData(context) }

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
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = destination.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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

fun readDestinationsFromData(context: Context): List<Destination> {
    val list = mutableListOf<Destination>()
    try {
        context.assets.open("datawisata.txt").bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val p = line.split("|")
                if (p.size == 4) list.add(Destination(p[0], p[1], p[2], p[3]))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return list
}