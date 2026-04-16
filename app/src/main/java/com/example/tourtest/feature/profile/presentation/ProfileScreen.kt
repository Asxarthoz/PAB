package com.example.tourtest.feature.profile.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val profileData = androidx.compose.runtime.remember {
        readProfileFromInternal(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {Text("Profil Wisatawan")})
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon (
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height((24.dp)))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation =  2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileItem(label = "Nama Lengkap", value = profileData.name, icon = Icons.Rounded.Person)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileItem(label = "Alamat Email", value = profileData.email, icon = Icons.Rounded.Email)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    val maskedPassword = "*".repeat(profileData.pass.length).ifEmpty { "********" }
                    ProfileItem(label = "Kata Sandi", value = maskedPassword, icon = Icons.Rounded.Lock)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Keluar dari Akun")
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

data class ProfileData(val name: String, val email: String, val pass: String)

fun readProfileFromInternal(context: Context): ProfileData {
    return try {
        val fileName = "datauser.txt"
        val file = context.getFileStreamPath(fileName)

        if (file.exists()) {
            val content = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val parts = content.split("|")
            if (parts.size >= 3) {
                // parts[0] = Nama, parts[1] = Email, parts[2] = Password
                ProfileData(parts[0], parts[1], parts[2])
            } else {
                ProfileData("Data Error", "Format salah", "")
            }
        } else {
            ProfileData("Guest", "anonim@mail.com", "")
        }
    } catch (e: Exception) {
        ProfileData("Error", "Gagal memuat data", "")
    }
}