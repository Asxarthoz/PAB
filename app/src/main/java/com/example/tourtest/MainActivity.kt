package com.example.tourtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.tourtest.core.ComposeApp
import com.example.tourtest.feature.auth.manager.AuthManager
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
                ComposeApp()
            }
        }
    }
}