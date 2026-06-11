package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class WishlistResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("destination_id") val destinationId: Long
)