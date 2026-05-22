package com.example.tourtest.core

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.tourtest.core.data.UserSession
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
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.UIntArraySerializer

@Composable
fun ComposeApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userSession = remember { UserSession(context) }
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    val homePrefs = remember { context.getSharedPreferences("home_search_prefs", android.content.Context.MODE_PRIVATE) }
    val favPrefs = remember { context.getSharedPreferences("fav_search_prefs", android.content.Context.MODE_PRIVATE) }
    val itinPrefs = remember { context.getSharedPreferences("itin_search_prefs", android.content.Context.MODE_PRIVATE) }

    val currentUserIdByStore by userSession.userId.collectAsState(initial = null)// Jalankan pengecekan sekali saja saat app dibuka
    LaunchedEffect(currentUserIdByStore) {
        if (currentUserIdByStore != null && currentUserIdByStore != "GUEST") {
            // Jika ada ID tersimpan dan bukan Guest, sinkronkan ke AuthManager
            AuthManager.setCurrentUser(currentUserIdByStore!!)

            // Jika sekarang sedang di AuthRoute, otomatis pindah ke Home
            if (backStack.lastOrNull() == Routes.AuthRoute) {
                backStack.clear()
                backStack.add(Routes.HomeRoute)
            }
        }
    }

    val homepageViewModel: HomepageViewModel = viewModel {
        HomepageViewModel(
            application = application,
            getAllDestinations = { HomepageManager.readDestinationsFromData(context) },
            sharedPrefs = homePrefs
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
            homepageManager = HomepageManager,
            sharedPrefs = favPrefs,
            userSession = userSession
        )
    }

    val itineraryViewModel: ItineraryViewModel = viewModel {
        ItineraryViewModel(
            application = application,
            itineraryManager = ItineraryManager,
            homepageManager = HomepageManager,
            sharedPrefs = itinPrefs,
            userSession = userSession
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
                                userSession = userSession ,
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

                        entry<Routes.FavoriteRoute> {
                            FavoriteScreen(
                                viewModel = favoriteViewModel,
                                userSession = userSession,
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
                                userSession = userSession,
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
                            val currentUserIdFromStore by userSession.userId.collectAsState(initial = null)
                            val currentUserId = currentUserIdFromStore ?: "GUEST"
                            val notificationViewModel: NotificationViewModel = viewModel(
                                key = currentUserId,
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
                                userSession  = userSession,
                                onBack = { backStack.removeLastOrNull() },
                                onNavigateToLogin = {
                                    backStack.add(Routes.AuthRoute)
                                }
                            )
                        }

                        entry<Routes.ProfileRoute> {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                userSession = userSession,
                                onLogout = {
                                    scope.launch {
                                        userSession.clearSession()
                                        AuthManager.logout()
                                        backStack.clear()
                                        backStack.add(Routes.AuthRoute)
                                    }
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
                            val currentUserIdFromStore by userSession.userId.collectAsState(initial = "GUEST")
                            val currentUserId = currentUserIdFromStore ?: "GUEST"
                            val detailViewModel: DetailViewModel = viewModel(
                                key = "${destinationId}_${currentUserId}",
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
                                userSession = userSession,
                                onBack = { backStack.removeLastOrNull() },
                                onNavigateToLogin = {
                                    backStack.add(Routes.AuthRoute)
                                }
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
                                userSession = userSession,
                                profileManager = profileManager
                            )
                        }

                        entry<Routes.ChangePasswordRoute> {
                            val passwordManager = PasswordManager(context)
                            ChangePasswordScreen(
                                onBack = { backStack.removeLastOrNull() },
//                                userSession = userSession,
                                passwordManager = passwordManager
                            )
                        }
                    }
                )
            }
        }
    }
}