package com.example.tourtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
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

        setContent {
            val navController = rememberNavController()
            Surface(color = Color.White) {
                NavHost(
                    navController = navController,
                    startDestination = "auth"
                ) {
                    composable("auth") {
                        AuthScreen(
                            onLoginSuccess = {
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
                        })
                    }

                    composable("profile") {
                        ProfileScreen(
                            onLogout = {
                                navController.navigate("auth") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }

            }
        }
    }
}

