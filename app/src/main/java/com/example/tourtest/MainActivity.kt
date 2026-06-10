package com.example.tourtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.tourtest.core.ComposeApp
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.ui.theme.TourizmeBlueMain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = TourizmeBlueMain.toArgb(),
                darkScrim = TourizmeBlueMain.toArgb()
            )
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            try {
                AuthManager.initializeDataFromAssets(this@MainActivity)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime < 1000) {
                    delay(1000 - elapsedTime)
                }

                withContext(Dispatchers.Main) {
                    keepSplashScreen = false
                }
            }
        }

        setContent {
            Surface(color = Color.White) {
                ComposeApp()
            }
        }
    }
}