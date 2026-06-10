package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ItineraryResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("start_date") val startDate: String, // Kolom ERD web
    @SerializedName("end_date") val endDate: String,     // Kolom ERD web
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("itinerary_items") val items: List<ItineraryItemResponse>? = emptyList()
)
