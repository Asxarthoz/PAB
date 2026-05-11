
package com.example.tourtest.core

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.detaildestination.presentation.DetailDestination
import com.example.tourtest.feature.detaildestination.viewmodel.DetailViewModel
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.homepage.viewmodel.HomepageViewModel
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.itinerary.presentation.ItineraryScreen
import com.example.tourtest.feature.itinerary.viewmodel.ItineraryViewModel
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import com.example.tourtest.feature.profile.presentation.ChangePasswordScreen
import com.example.tourtest.feature.profile.presentation.EditProfileScreen
import com.example.tourtest.feature.profile.presentation.FullScreenImageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen
import com.example.tourtest.feature.profile.viewmodel.ProfileViewModel
import com.example.tourtest.feature.wishlist.manager.WishlistManager
import com.example.tourtest.feature.wishlist.presentation.WishListScreen
import com.example.tourtest.feature.wishlist.viewmodel.WishlistViewModel
import com.example.tourtest.ui.theme.TourizmeTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.AuthRoute)
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val homepageViewModel: HomepageViewModel = viewModel {
        HomepageViewModel(
            getAllDestinations = { HomepageManager.readDestinationsFromData(context) }
        )
    }

    val profileViewModel: ProfileViewModel = viewModel {
        ProfileViewModel(
            profileManager = ProfileManager(context)
        )
    }

    val wishlistViewModel: WishlistViewModel = viewModel {
        WishlistViewModel(
            application = application,
            wishlistManager = WishlistManager,
            homepageManager = HomepageManager
        )
    }

    val itineraryViewModel: ItineraryViewModel = viewModel {
        ItineraryViewModel(
            application = application,
            itineraryManager = ItineraryManager,
            homepageManager = HomepageManager
        )
    }

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
                        viewModel = homepageViewModel,
                        onNavigateToProfile = { backStack.add(Routes.ProfileRoute) },
                        onNavigateToDetail = { id ->
                            backStack.add(Routes.DetailRoute(destinationId = id))
                        }
                    )
                }

                entry<Routes.ProfileRoute> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogout = {
                            AuthManager.logout()
                            backStack.clear()
                            backStack.add(Routes.AuthRoute)
                        },
                        onNavigateToEditProfile = { backStack.add(Routes.EditProfileRoute) },
                        onNavigateToChangePassword = { backStack.add(Routes.ChangePasswordRoute) },
                        onNavigateToFullScreenImage = { backStack.add(Routes.FullScreenImageRoute) },
                        onNavigateToWishlist = { backStack.add(Routes.WishlistRoute) },
                        onNavigateToItinerary = { backStack.add(Routes.ItineraryRoute) }
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
                        viewModel = wishlistViewModel,
                        onNavigateToDetail = { id ->
                            backStack.add(Routes.DetailRoute(destinationId = id))
                        }
                    )
                }

                entry<Routes.ItineraryRoute> {
                    ItineraryScreen(
                        viewModel = itineraryViewModel,
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
                    val detailViewModel: DetailViewModel = viewModel {
                        DetailViewModel(
                            application = application,
                            destinationId = route.destinationId,
                            currentUserId = AuthManager.getCurrentUserId() ?: "",
                            wishlistManager = WishlistManager,
                            itineraryManager = ItineraryManager
                        )
                    }

                    DetailDestination(
                        viewModel = detailViewModel,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}