package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class DestinationResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("location") val location: String,
    @SerializedName("price") val price: String,
    @SerializedName("image") val image: String,
    @SerializedName("gmaps") val gmaps: String,
    @SerializedName("rating") val rating: Float?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,

    // 💡 Relasi Tambahan dari ERD:
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("reviews") val reviews: List<ReviewResponse>? = emptyList(),
    @SerializedName("image_galleries") val imageGalleries: List<ImageGalleryResponse>? = emptyList()
)
