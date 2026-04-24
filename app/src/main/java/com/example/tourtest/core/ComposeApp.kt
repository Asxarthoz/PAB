package com.example.tourtest.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.detaildestination.presentation.DestinationDetailScreen
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import com.example.tourtest.feature.profile.presentation.ChangePasswordScreen
import com.example.tourtest.feature.profile.presentation.EditProfileScreen
import com.example.tourtest.feature.profile.presentation.FullScreenImageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen
import com.example.tourtest.feature.wishlist.manager.WishlistManager
import com.example.tourtest.feature.wishlist.presentation.WishListScreen
import com.example.tourtest.ui.theme.TourizmeTheme
import androidx.compose.runtime.setValue
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.itinerary.presentation.ItineraryScreen

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.AuthRoute)
    val context = androidx.compose.ui.platform.LocalContext.current

    TourizmeTheme {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Routes.AuthRoute> {
                    AuthScreen(
                        onLoginSuccess = { backStack.add(Routes.HomeRoute) }
                    )
                }

                entry<Routes.HomeRoute> {
                    HomepageScreen(
                        onNavigateToProfile = { backStack.add(Routes.ProfileRoute) },
                        onNavigateToDetail = { id ->
                            backStack.add(Routes.DetailRoute(destinationId = id))
                        }
                    )
                }

                entry<Routes.ProfileRoute> {
                    ProfileScreen(
                        onLogout = {
                            backStack.clear()
                            backStack.add(Routes.AuthRoute)
                        },
                        onNavigateToEditProfile = { backStack.add(Routes.EditProfileRoute) },
                        onNavigateToChangePassword = { backStack.add(Routes.ChangePasswordRoute) },
                        onNavigateToFullScreenImage = { backStack.add(Routes.FullScreenImageRoute) },
                        onNavigateToWishlist = {backStack.add(Routes.WishlistRoute)},
                        onNavigateToItinerary = {backStack.add(Routes.ItineraryRoute)}
                    )
                }

                entry<Routes.EditProfileRoute> {
                    val profileManager = ProfileManager(context)
                    EditProfileScreen(
                        onBack = { backStack.removeLastOrNull() },
                        profileManager = profileManager
                    )
                }

                entry<Routes.WishlistRoute> {
                    WishListScreen(
                        onNavigateToDetail = { id ->
                            backStack.add(Routes.DetailRoute(destinationId = id))
                        }
                    )
                }


                entry<Routes.ItineraryRoute> {
                    ItineraryScreen(
                        onNavigateToDetail = { id ->
                            backStack.add(Routes.DetailRoute(destinationId = id))
                        }
                    )
                }

                entry<Routes.ChangePasswordRoute> {
                    val passwordManager = PasswordManager(context)
                    ChangePasswordScreen(
                        onBack = { backStack.removeLastOrNull() },
                        passwordManager = passwordManager
                    )
                }
                entry<Routes.FullScreenImageRoute> {
                    FullScreenImageScreen(
                        onBack = { backStack.removeLastOrNull() },
                        imageBitmap = null
                    )
                }

                entry<Routes.DetailRoute> { route ->
                    val context = LocalContext.current
                    val currentUserId = AuthManager.getCurrentUserId() ?:""

                    var wishListIds by remember { mutableStateOf(setOf<String>()) }
                    var itineraryListIds by remember { mutableStateOf(setOf<String>()) }

                    LaunchedEffect(currentUserId) {
                        wishListIds = WishlistManager.getAllWish(context)
                            .filter { it.userId == currentUserId }
                            .map { it.destinationId }
                            .toSet()

                        itineraryListIds = com.example.tourtest.feature.itinerary.manager.ItineraryManager.getAllItinerary(context)
                            .filter { it.userId == currentUserId }
                            .map { it.destinationId }
                            .toSet()
                    }

                    val isFavorite = wishListIds.contains(route.destinationId)
                    val isPlanned = itineraryListIds.contains(route.destinationId)

                    DestinationDetailScreen(
                        destinationId = route.destinationId,
                        isWishlisted = isFavorite,
                        isItineraried = isPlanned,
                        onWishListClick = {
                            if (isFavorite) {
                                WishlistManager.removeDestination(context, currentUserId, route.destinationId)
                                wishListIds = wishListIds - route.destinationId
                            } else {
                                WishlistManager.addDestination(context, currentUserId, route.destinationId)
                                wishListIds = wishListIds + route.destinationId
                            }
                        },
                        onItineraryClick = { selectedDate ->
                            ItineraryManager.addDestination(
                                context, currentUserId, route.destinationId, selectedDate
                            )
                            itineraryListIds = itineraryListIds + route.destinationId
                        },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}