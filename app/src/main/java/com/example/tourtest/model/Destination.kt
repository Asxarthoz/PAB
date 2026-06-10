package com.example.tourtest.model

data class Destination(
    val id: String,
    val name: String,
    val location: String,
    val price: String,
    val imageUrl: String,
    val gmapUrl: String,
    val description: String,
    val averageRating: Float = 0.0f,
    val reviews: List<Review> = emptyList(),
    val openTime: String = "00:00",
    val closeTime: String = "00:00"
)
