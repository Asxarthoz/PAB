package com.example.tourtest.model

data class Review (
    val userId: String,
    val userName: String,
    val ratingGiven: Float,
    val comment: String
)