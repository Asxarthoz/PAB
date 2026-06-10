package com.example.tourtest.core.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
//import coil.compose.AsyncImage
import com.example.tourtest.R
import com.example.tourtest.model.Destination
import com.example.tourtest.ui.theme.InterFontFamily
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBlueMain
import kotlin.String

@Composable
fun DestinationCardContent(
    destination: Destination,
    isWishlisted: Boolean,
    isItineraried: Boolean,
    onWishListClick: () -> Unit,
    onItineraryClick: () -> Unit,
    onMapsClick: () -> Unit,
    onClick:() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box {
                SubcomposeAsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop,
//                    placeholder = painterResource(R.drawable.undraw_loading_ui_egb4),
//                    error = painterResource(R.drawable.undraw_loading_ui_egb4),
                    loading = {
                        ShimmerPlaceholder()
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF1F4FA)),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Gagal memuat gambar",
                                tint = Color.Gray
                            )
                        }
                    }
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
                        onClick = onItineraryClick,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Itinerary",
                            tint = if (isItineraried) TourizmeBlueMain else Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = destination.name,
                        fontSize = 20.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color(0xffffc107),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = destination.averageRating.toString(),
                            fontSize = 16.sp,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xff3f4850)
                        )
                    }
                }

                TextButton(
                    onClick = onMapsClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = TourizmeBlueMain
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = destination.location,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "Rp ${destination.price} / Org",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = TourizmeBlueMain,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun DestinationCard(
    destination: Destination,
    isWishlisted: Boolean,
    isItineraried: Boolean,
    onWishListClick: () -> Unit,
    onItineraryClick: () -> Unit,
    onClick:() -> Unit
) {
    val context = LocalContext.current

    val openMaps = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destination.gmapUrl))
        context.startActivity(intent)
    }

    DestinationCardContent(
        destination = destination,
        isWishlisted = isWishlisted,
        isItineraried = isItineraried,
        onWishListClick = onWishListClick,
        onItineraryClick = onItineraryClick,
        onMapsClick = openMaps,
        onClick = onClick
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun DestinationCardPreview() {
    val dummyDestination = Destination(
        id = "1",
        name = "Pantai Parangtritis",
        location = "Bantul, Yogyakarta",
        price = "15.000",
        imageUrl = "https://example.com/image.jpg",
        gmapUrl = "https://maps.google.com",
        description = "Deskripsi Pantai Parangtritis",
        averageRating = 4.7f,
        reviews = emptyList()
    )

    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DestinationCardContent(
                destination = dummyDestination,
                isWishlisted = true,
                isItineraried = true,
                onWishListClick = {},
                onItineraryClick = {},
                onMapsClick = {},
                onClick = {}
            )
        }
    }
}
