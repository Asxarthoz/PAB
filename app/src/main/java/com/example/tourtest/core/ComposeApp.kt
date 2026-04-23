package com.example.tourtest.core

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import com.example.tourtest.feature.profile.presentation.ChangePasswordScreen
import com.example.tourtest.feature.profile.presentation.EditProfileScreen
import com.example.tourtest.feature.profile.presentation.FullScreenImageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen
import com.example.tourtest.ui.theme.TourizmeTheme

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
                        onNavigateToProfile = { backStack.add(Routes.ProfileRoute) }
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
                        onNavigateToFullScreenImage = { backStack.add(Routes.FullScreenImageRoute) }
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
                entry<Routes.FullScreenImageRoute> {
                    FullScreenImageScreen(
                        onBack = { backStack.removeLastOrNull() },
                        imageBitmap = null
                    )
                }
            }
        )
    }
}