package com.example.tourtest

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx. compose. ui. unit. *
import androidx. compose. runtime. remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.*
import androidx.navigation.NavController


class MainActivity : ComponentActivity() {

    var nama = ""
    fun Greetings(a:String) = "Hai $a"


    @Composable
    fun Login() {
        Column (modifier = Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            var text by remember{mutableStateOf("")}
            var passw by remember{mutableStateOf("")}
            TextField(
                value = text,
                onValueChange = { newText -> text = newText },
                label = {Text("Username")}
            )
            TextField(
                value = passw,
                onValueChange = { newText -> passw = newText },
                label = {Text("Password")}
            )

            Button(onClick = {}) {
                Text("Submit")
            }

        }
    }

    @Composable
    fun Home(navController: NavController) {
        Column (modifier = Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "Tourizme",
                fontSize = 20.sp, fontWeight = FontWeight.Bold
            )

            Image(
                painter = painterResource(id = R.drawable.rajaampat),
                contentDescription = "Raja Ampat",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Bosenkan di rumah mulu? Stress juga gara-gara ngoding mulu? Mending liburan bersama Tourizme! Buat liburan kamu jadi praktis" +
                        "dan enjoy dengan Tourizme.",
                fontSize = 18.sp

            )


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            Surface (color = Color.White){
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        Home(navController)
                    }
                }
            }

        }
    }
}

