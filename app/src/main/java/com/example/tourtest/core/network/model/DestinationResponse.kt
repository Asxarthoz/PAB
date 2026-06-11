package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class DestinationResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("location") val location: String,
    @SerializedName("price") val price: Long,
    @SerializedName("average_rating") val averageRating: Double?,
    @SerializedName("description") val description: String,
    @SerializedName("open_time") val openTime: String,
    @SerializedName("close_time") val closeTime: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("images") val images: List<String>?,
    @SerializedName("category") val category: CategoryResponse?,
    @SerializedName("bisnis_owner") val bisnisOwner: BisnisOwnerResponse?,
    @SerializedName("reviews") val reviews: List<ReviewResponse>?
)

data class CategoryResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)

data class BisnisOwnerResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("nik") val nik: String,
    @SerializedName("ktp_photo") val ktpPhoto: String?,
    @SerializedName("nib") val nib: String?,
    @SerializedName("verification_status") val verificationStatus: Boolean
)