package com.example.tourtest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

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