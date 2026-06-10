package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("destination_id") val destinationId: String,
    @SerializedName("rating") val rating: Float,       // Di web kolomnya 'rating'
    @SerializedName("comment") val comment: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,

    // Menyertakan objek user milik web untuk mengambil nama pengulas di UI mobile
    @SerializedName("user") val user: UsersResponse?
)
