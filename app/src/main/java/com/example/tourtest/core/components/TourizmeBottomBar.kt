package com.example.tourtest.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.tourtest.core.LocalBackStack
import com.example.tourtest.core.Routes

@Composable
fun TourizmeBottombar(
    currentRoute: Any?
) {
    val backStack = LocalBackStack.current

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 8.dp
    ) {
        val navigationItems = listOf(
            Routes.HomeRoute to Pair(Icons.Default.Home, "Home"),
            Routes.ItineraryRoute to Pair(Icons.Default.DateRange, "Rencana"),
            Routes.FavoriteRoute to Pair(Icons.Default.Favorite, "Favorit"),
            Routes.ProfileRoute to Pair(Icons.Default.Person, "Profil")
        )

        navigationItems.forEach { (route, pair) ->
            val (icon, label) = pair
            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        backStack.clear()
                        backStack.add(route)
                    }
                },
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(label) }
            )
        }
    }
}