package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ItineraryItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("itinerary_id") val itineraryId: String,
    @SerializedName("destination_id") val destinationId: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,

    // Menyertakan data destinasi di dalam item itinerary
    @SerializedName("destination") val destination: DestinationResponse?
)
