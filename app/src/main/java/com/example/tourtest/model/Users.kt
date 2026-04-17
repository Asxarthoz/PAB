package com.example.tourtest.model

data class Users(
    val id: String,
    val name: String,
    val nickName: String,
    val email: String,
    val password: String,
    val role: String, //  admin/mitra/ wisatwaan
    val isVerified: Boolean
)
