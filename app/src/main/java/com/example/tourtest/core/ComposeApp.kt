package com.example.tourtest.core

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.example.tourtest.core.components.TourizmeBottombar
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.auth.viewmodel.AuthViewModel
import com.example.tourtest.feature.detaildestination.presentation.DestinationDetailScreen
import com.example.tourtest.feature.detaildestination.viewmodel.DetailViewModel
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.profile.presentation.ChangePasswordScreen
import com.example.tourtest.feature.profile.presentation.EditProfileScreen
import com.example.tourtest.feature.profile.presentation.FullScreenImageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen
import com.example.tourtest.feature.favorite.presentation.FavoriteScreen
import com.example.tourtest.feature.favorite.viewmodel.FavoriteViewModel
import com.example.tourtest.feature.homepage.viewmodel.HomepageViewModel
import com.example.tourtest.feature.itinerary.presentation.ItineraryScreen
import com.example.tourtest.feature.itinerary.viewmodel.ItineraryViewModel
import com.example.tourtest.feature.notification.presentation.NotificationScreen
import com.example.tourtest.feature.notification.viewmodel.NotificationViewModel
import com.example.tourtest.feature.profile.viewmodel.ChangePasswordViewModel
import com.example.tourtest.feature.profile.viewmodel.ProfileViewModel
import com.example.tourtest.ui.theme.TourizmeTheme
import kotlinx.coroutines.launch

@Composable
fun ComposeApp() {
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    val currentUserIdByStore by userSession.userId.collectAsState(initial = null)
    LaunchedEffect(currentUserIdByStore) {
        if (currentUserIdByStore != null) {
            AuthManager.setCurrentUser(currentUserIdByStore!!)
        } else {
            AuthManager.setCurrentUser("GUEST")
        }

        if (backStack.lastOrNull() == Routes.AuthRoute) {
            backStack.clear()
            backStack.add(Routes.HomeRoute)
        }
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
                            val authViewModel: AuthViewModel = hiltViewModel()
                            AuthScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    backStack.clear()
                                    backStack.add(Routes.HomeRoute )
                                }
                            )
                        }

                        entry<Routes.HomeRoute> {
                            val homepageViewModel: HomepageViewModel = hiltViewModel()
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
                            val favoriteViewModel: FavoriteViewModel = hiltViewModel()
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
                            val itineraryViewModel: ItineraryViewModel = hiltViewModel()
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
                            val notificationViewModel: NotificationViewModel = hiltViewModel()
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
                            val profileViewModel: ProfileViewModel = hiltViewModel()
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
                            val detailViewModel: DetailViewModel = hiltViewModel()

                            LaunchedEffect(destinationId) {
                                detailViewModel.initializeData(destinationId)
                            }

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
                            val profileViewModel: ProfileViewModel = hiltViewModel()
                            FullScreenImageScreen(
                                onBack = { backStack.removeLastOrNull() },
                                viewModel = profileViewModel
                            )
                        }

                        entry<Routes.EditProfileRoute> {
//                            val profileManager = ProfileManager(context)
                            val profileViewModel: ProfileViewModel = hiltViewModel()

                            EditProfileScreen(
                                onBack = { backStack.removeLastOrNull() },
                                userSession = userSession,
                                viewModel = profileViewModel
                            )
                        }

                        entry<Routes.ChangePasswordRoute> {
                            val changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
                            ChangePasswordScreen(
                                onBack = { backStack.removeLastOrNull() },
                                viewModel = changePasswordViewModel
                            )
                        }
                    }
                )
            }
        }
    }
}