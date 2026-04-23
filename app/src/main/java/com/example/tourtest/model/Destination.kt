package com.example.tourtest.model

data class Destination(
    // Id masih manual, belum kaya id user yang digenerate
    val id: String,
    val name: String,
    val location: String,
    val price: String,
    val imageUrl: String,
    val gmapUrl: String,
    val description: String
)
