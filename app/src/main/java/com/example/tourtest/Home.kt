package com.example.tourtest

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Home(navController: NavController) {
    val context = LocalContext.current
    Column (modifier = Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = "Tourizme",
            fontSize = 20.sp, fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.rajaampat),
            contentDescription = "Raja Ampat",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Bosenkan di rumah mulu? Stress juga gara-gara ngoding mulu? Mending liburan bersama Tourizme! Buat liburan kamu jadi praktis" +
                    " dan enjoy dengan Tourizme.",
            fontSize = 18.sp, textAlign = TextAlign.Center

        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("profile")
        }) {
            Text("Profilku")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Asxarthoz")
            )
            context.startActivity(intent)
        }) {
            Text("Githubku")
        }


    }
}