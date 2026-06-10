package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class WishlistResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("destination_id") val destinationId: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
