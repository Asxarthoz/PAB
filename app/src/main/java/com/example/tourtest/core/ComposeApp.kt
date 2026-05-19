package com.example.tourtest.core

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.example.tourtest.core.components.TourizmeBottombar
import com.example.tourtest.database.notification.AppDatabase
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.auth.viewmodel.AuthViewModel
import com.example.tourtest.feature.detaildestination.presentation.DestinationDetailScreen
import com.example.tourtest.feature.detaildestination.viewmodel.DetailViewModel
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import com.example.tourtest.feature.profile.presentation.ChangePasswordScreen
import com.example.tourtest.feature.profile.presentation.EditProfileScreen
import com.example.tourtest.feature.profile.presentation.FullScreenImageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.favorite.presentation.FavoriteScreen
import com.example.tourtest.feature.favorite.viewmodel.FavoriteViewModel
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.homepage.viewmodel.HomepageViewModel
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.itinerary.presentation.ItineraryScreen
import com.example.tourtest.feature.itinerary.viewmodel.ItineraryViewModel
import com.example.tourtest.feature.notification.manager.NotificationManager
import com.example.tourtest.feature.notification.presentation.NotificationScreen
import com.example.tourtest.feature.notification.viewmodel.NotificationViewModel
import com.example.tourtest.feature.profile.viewmodel.ProfileViewModel
import com.example.tourtest.ui.theme.TourizmeTheme

@Composable
fun ComposeApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val backStack = rememberNavBackStack(Routes.AuthRoute)

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

    val favoriteViewModel: FavoriteViewModel = viewModel {
        FavoriteViewModel(
            application = application,
            favoriteManager = FavoriteManager,
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

    val authViewModel: AuthViewModel = viewModel {
        AuthViewModel(
            application = application
        )
    }

    val currentScreen = backStack.lastOrNull()

    TourizmeTheme {
        CompositionLocalProvider(LocalBackStack provides backStack) {
            Scaffold(
                bottomBar = {
                    val mainRoutes = listOf(
                        Routes.HomeRoute,
                        Routes.ItineraryRoute,
                        Routes.FavoriteRoute,
                        Routes.ProfileRoute
                    )
                    if (currentScreen in mainRoutes) {
                        TourizmeBottombar(
                            currentRoute = currentScreen
                        )
                    }
                }
            ) { innerPadding ->
                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.padding(innerPadding),
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<Routes.AuthRoute> {
                            AuthScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    backStack.clear()
                                    backStack.add(Routes.HomeRoute)
                                }
                            )
                        }

                        entry<Routes.HomeRoute> {
                            HomepageScreen(
                                viewModel = homepageViewModel,
                                onNavigateToDetail = { id ->
                                    backStack.add(Routes.DetailRoute(destinationId = id))
                                },
                                onNavigateToNotification = {
                                    backStack.add(Routes.NotificationRoute)
                                }
                            )
                        }

                        entry<Routes.FavoriteRoute> {
                            FavoriteScreen(
                                viewModel = favoriteViewModel,
                                onNavigateToDetail = { id ->
                                    backStack.add(Routes.DetailRoute(destinationId = id))
                                },
                                onNavigateToNotification = {
                                    backStack.add(Routes.NotificationRoute)
                                },
                                onNavigateToLogin = {
                                    backStack.add(Routes.AuthRoute)
                                }
                            )
                        }

                        entry<Routes.ItineraryRoute> {
                            ItineraryScreen(
                                viewModel = itineraryViewModel,
                                onNavigateToDetail = { id ->
                                    backStack.add(Routes.DetailRoute(destinationId = id))
                                },
                                onNavigateToNotification = {
                                    backStack.add(Routes.NotificationRoute)
                                },
                                onNavigateToLogin = {
                                    backStack.add(Routes.AuthRoute)
                                }
                            )
                        }

                        entry<Routes.NotificationRoute> {
                            val currentUserId = AuthManager.getCurrentUserId() ?: ""
                            val notificationViewModel: NotificationViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun<T : ViewModel> create(modelClass: Class<T>): T {
                                        return NotificationViewModel(
                                            application = application,
                                            userId = currentUserId,
                                            notificationDao = AppDatabase.getDatabase(context).notificationDao(),
                                            itineraryManager = ItineraryManager,
                                            notificationManager = NotificationManager,
                                            homepageManager = HomepageManager
                                        ) as T
                                    }
                                }
                            )
                            NotificationScreen(
                                viewModel = notificationViewModel,
                                onBack = { backStack.removeLastOrNull() },
                                onNavigateToLogin = {
                                    backStack.add(Routes.AuthRoute)
                                }
                            )
                        }

                        entry<Routes.ProfileRoute> {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onLogout = {
                                    backStack.clear()
                                    backStack.add(Routes.AuthRoute)
                                },
                                onNavigateToEditProfile = {
                                    backStack.add(Routes.EditProfileRoute)
                                },
                                onNavigateToChangePassword = {
                                    backStack.add(Routes.ChangePasswordRoute)
                                },
                                onNavigateToFullScreenImage = {
                                    backStack.add(Routes.FullScreenImageRoute)
                                },
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }

                        entry<Routes.DetailRoute> { route ->
                            val destinationId = route.destinationId
                            val currentUserId = AuthManager.getCurrentUserId() ?: ""
                            val detailViewModel: DetailViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return DetailViewModel(
                                            application = application,
                                            destinationId = destinationId,
                                            currentUserId = currentUserId,
                                            homepageManager = HomepageManager,
                                            favoriteManager = FavoriteManager,
                                            itineraryManager = ItineraryManager
                                        ) as T
                                    }
                                }
                            )

                            DestinationDetailScreen(
                                viewModel = detailViewModel,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }

                        entry<Routes.FullScreenImageRoute> {
                            FullScreenImageScreen(
                                onBack = { backStack.removeLastOrNull() },
                                imageBitmap = null
                            )
                        }

                        entry<Routes.EditProfileRoute> {
                            val profileManager = ProfileManager(context)
                            EditProfileScreen(
                                onBack = { backStack.removeLastOrNull() },
                                profileManager = profileManager
                            )
                        }

                        entry<Routes.ChangePasswordRoute> {
                            val passwordManager = PasswordManager(context)
                            ChangePasswordScreen(
                                onBack = { backStack.removeLastOrNull() },
                                passwordManager = passwordManager
                            )
                        }
                    }
                )
            }
        }
    }
}