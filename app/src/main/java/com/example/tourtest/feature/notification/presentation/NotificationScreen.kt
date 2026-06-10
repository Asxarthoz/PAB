package com.example.tourtest.feature.notification.presentation

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import android.Manifest
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tourtest.core.components.TourizmeEmptyState
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.notification.viewmodel.NotificationViewModel
import com.example.tourtest.model.NotificationHistory
import com.example.tourtest.ui.theme.MontserratFontFamily
import com.example.tourtest.ui.theme.TourizmeBlueMain


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationContent(
    notifications: List<NotificationHistory>,
    onBack: () -> Unit,
    emptyMessage: String = "Belum ada notifikasi baru untuk anda"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontFamily = MontserratFontFamily, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TourizmeBlueMain,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            TourizmeEmptyState(emptyMessage)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationGuestContent(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontFamily = MontserratFontFamily, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TourizmeBlueMain,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TourizmeEmptyState(
                message = "Anda belum Login",
                subMessage = "Silahkan Login untuk melihat riwayat notifikasi perjalanan anda",
                imageVector = Icons.Default.Lock,
                actionButton = {
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Login Sekarang!")
                    }
                }
            )

        }
    }
}


@Composable
fun NotificationItem(
    notification: NotificationHistory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = TourizmeBlueMain,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pengingat Perjalanan",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    userSession: UserSession,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val notifications by viewModel.notification.collectAsStateWithLifecycle()
    val userIdFromStore by userSession.userId.collectAsState(initial = null)
    val currentUserId = userIdFromStore?: "GUEST"
    val isGuest = currentUserId == null || currentUserId == "GUEST"

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract  = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (!isGuest) {
                viewModel.loadNotifications()
            }
        } else {
            Toast.makeText(
                context,
                "Izin ditolak. Anda tidak akan menerima banner pengingat perjalanan kecuali izin dinyalakan.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            if (!isGuest) {
                viewModel.loadNotifications()
            }
        }
    }

    if (currentUserId == "GUEST" || currentUserId.isBlank()) {
        NotificationGuestContent(
            onBack = onBack,
            onNavigateToLogin = onNavigateToLogin
        )
    } else {
        NotificationContent(
            notifications = notifications,
            onBack = onBack
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun NotificationPreview() {
    MaterialTheme {
        NotificationContent(
            notifications = listOf(
                NotificationHistory("1", "H-3: Wah, sebentar lagi kamu berangkat ke Keraton Surakarta!", "dest_1", System.currentTimeMillis()),
                NotificationHistory("2", "6 Jam Lagi: Siap-siap, perjalananmu ke Kost Putra Barokah akan segera dimulai!", "dest_2", System.currentTimeMillis())
            ),
            onBack = {}
        )
    }
}
