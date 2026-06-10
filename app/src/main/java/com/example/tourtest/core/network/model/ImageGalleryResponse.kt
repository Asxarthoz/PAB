package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ImageGalleryResponse(
    @SerializedName("id") val id: String,
    @SerializedName("destination_id") val destinationId: String,
    @SerializedName("image") val imagePath: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
