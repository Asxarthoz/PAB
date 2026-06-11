package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ImageGalleryResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("destination_id") val destinationId: Long,
    @SerializedName("path") val path: String
)