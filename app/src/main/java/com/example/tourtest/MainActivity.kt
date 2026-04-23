package com.example.tourtest

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import com.example.tourtest.feature.profile.presentation.ChangePasswordScreen
import com.example.tourtest.feature.profile.presentation.EditProfileScreen
import com.example.tourtest.feature.profile.presentation.FullScreenImageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen
import com.example.tourtest.feature.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        lifecycleScope.launch {
            delay(1000)
            keepSplashScreen = false
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AuthManager.initializeDataFromAssets(this)

        setContent {
            Surface(color = Color.White) {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current

    var isLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn()) }

    NavHost(
        navController = navController,
        startDestination = Screen.Auth
    ) {
        // ✨ 1. Auth Screen
        composable<Screen.Auth> {
            AuthScreen(
                navController = navController  // ✨ Kirim navController
            )
        }

        // ✨ 2. Home Screen
        composable<Screen.Home> {
            HomepageScreen(
                navController = navController  // ✨ Kirim navController
            )
        }

        // ✨ 3. Profile Screen
        composable<Screen.Profile> {
            ProfileScreen(
                onLogout = {
                    AuthManager.logout()
                    isLoggedIn = false
                    navController.navigate(Screen.Auth) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // ✨ 4. Edit Profile Screen
        composable<Screen.EditProfile> {
            val profileManager = remember { ProfileManager(context) }
            LaunchedEffect(Unit) {
                profileManager.loadUserFromFile()
            }
            EditProfileScreen(
                navController = navController,
                profileManager = profileManager
            )
        }

        // ✨ 5. Full Screen Image
        composable<Screen.FullScreenImage> {
            val profileManager = remember { ProfileManager(context) }
            val profileImagePath by profileManager.profileImagePath.collectAsStateWithLifecycle()
            var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

            LaunchedEffect(profileImagePath) {
                if (profileImagePath != null) {
                    imageBitmap = profileManager.loadProfileImage(profileImagePath)
                }
            }

            FullScreenImageScreen(
                navController = navController,
                imageBitmap = imageBitmap
            )
        }

        composable<Screen.ChangePassword> {
            val passwordManager = remember { PasswordManager(context) }
            ChangePasswordScreen(
                navController = navController,
                passwordManager = passwordManager
            )
        }

    }
}