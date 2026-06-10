package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("image") val image: String?, // Foto profil user di web
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
