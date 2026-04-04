package com.example.tourtest

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth

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
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.*
import androidx.navigation.NavController
import androidx. compose. foundation. layout. *
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {


    var nama = ""
    fun Greetings(a:String) = "Hai $a"


    @Composable
    fun Login(navController: NavController) {
        Column (modifier = Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            var username by remember{mutableStateOf("")}
            var passw by remember{mutableStateOf("")}
            var errorMes by remember { mutableStateOf("") }
            TextField(
                value = username,
                onValueChange = { newText -> username = newText },
                label = {Text("Username")}
            )
            TextField(
                value = passw,
                onValueChange = { newText -> passw = newText },
                label = {Text("Password")}
            )

            Button(onClick = {
                if(username == "admin" && passw == "123") {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                } else {
                    errorMes = "Username atau password salah!"
                }
            }) {
                Text("Submit")
            }

            if(errorMes.isNotEmpty()) {
                Text(
                    text = errorMes,
                    color = Color.Red
                )
            }

        }
    }

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
                        "dan enjoy dengan Tourizme.",
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

    @Composable
    fun Profile(navController: NavController) {
        Column (modifier = Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painter = painterResource(id = R.drawable.rajaampat),
                contentDescription = "tungtungtungsahur",
                modifier = Modifier.size(200.dp).clip(CircleShape).border(1.dp, Color.Black, CircleShape)

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "L0124067",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
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
                    startDestination = "login"
                ) {

                    composable ("login"){
                        Login(navController)
                    }

                    composable("home") {
                        Home(navController)
                    }
                    composable("profile") {
                        Profile(navController)
                    }
                }
            }

        }
    }
}

