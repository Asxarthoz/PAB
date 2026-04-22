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

    val startDestination = if (isLoggedIn) "home" else "auth"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("auth") {
            AuthScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomepageScreen(
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onLogout = {
                    AuthManager.logout()
                    isLoggedIn = false
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable("edit_profile") {
            val profileManager = remember { ProfileManager(context) }
            LaunchedEffect(Unit) {
                profileManager.loadUserFromFile()
            }
            EditProfileScreen(
                navController = navController,
                profileManager = profileManager
            )
        }

        composable("full_screen_image") {
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
        composable("change_password") {
            val passwordManager = remember { PasswordManager(context) }
            ChangePasswordScreen(
                navController = navController,
                passwordManager = passwordManager
            )
        }
    }
}