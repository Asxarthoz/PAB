package com.example.tourtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

