package com.example.tourtest.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourtest.core.LocalBackStack
import com.example.tourtest.core.Routes
import androidx.navigation3.runtime.NavKey
import com.example.tourtest.ui.theme.MontserratFontFamily

private val HighlightGreen = Color(0xFF4EDEA3)

@Composable
fun TourizmeBottombarContent(
    currentRoute: Any?,
    onNavigateToRoute: (NavKey) -> Unit,
) {
    val navigationItems = listOf(
        Routes.HomeRoute     to Triple(Icons.Default.Home,      "Explore",   Routes.HomeRoute),
        Routes.ItineraryRoute to Triple(Icons.Default.DateRange, "Itinerary", Routes.ItineraryRoute),
        Routes.FavoriteRoute  to Triple(Icons.Default.Favorite,  "Favorite",  Routes.FavoriteRoute),
        Routes.ProfileRoute   to Triple(Icons.Default.Person,    "Profile",   Routes.ProfileRoute),
    )

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationItems.forEach { (route, triple) ->
                val (icon, label, _) = triple
                val isSelected = currentRoute == route

                BottomBarItem(
                    icon = icon,
                    label = label,
                    isSelected = isSelected,
                    onClick = { onNavigateToRoute(route) }
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(
                color = if (isSelected) HighlightGreen else Color.Transparent,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFF1A6B50) else Color(0xFF6B7280)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF1A6B50) else Color(0xFF6B7280),
                fontFamily = MontserratFontFamily
            )
        }
    }
}

@Composable
fun TourizmeBottombar(
    currentRoute: Any?
) {
    val backStack = LocalBackStack.current

    TourizmeBottombarContent(
        currentRoute = currentRoute,
        onNavigateToRoute = { route ->
            if (currentRoute != route) {
                backStack.clear()
                backStack.add(route)
            }
        }
    )
}

@Preview(name = "Profile Terpilih", showBackground = true)
@Composable
fun TourizmeBottombarProfilePreview() {
    MaterialTheme {
        TourizmeBottombarContent(
            currentRoute = Routes.ProfileRoute,
            onNavigateToRoute = {}
        )
    }
}

@Preview(name = "Home Terpilih", showBackground = true)
@Composable
fun TourizmeBottombarHomePreview() {
    MaterialTheme {
        TourizmeBottombarContent(
            currentRoute = Routes.HomeRoute,
            onNavigateToRoute = {}
        )
    }
}
