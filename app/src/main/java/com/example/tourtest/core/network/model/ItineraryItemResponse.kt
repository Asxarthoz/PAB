package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ItineraryItemResponse(
    // ========== PRIMARY ==========
    @SerializedName("id")
    val id: Long,  // 🔄 String → Long

    @SerializedName("itinerary_id")
    val itineraryId: Long,  // 🔄 String → Long

    @SerializedName("destination_id")
    val destinationId: Long,  // 🔄 String → Long

    // ========== ITINERARY ITEM DETAILS (sesuai ERD) ==========
    @SerializedName("day_number")  // ✅ TAMBAH: hari ke berapa
    val dayNumber: Int,

    @SerializedName("sequence_order")  // ✅ TAMBAH: urutan dalam hari
    val sequenceOrder: Int,

    @SerializedName("start_time")  // ✅ TAMBAH: waktu mulai (format: HH:MM:SS)
    val startTime: String?,

    @SerializedName("end_time")  // ✅ TAMBAH: waktu selesai (format: HH:MM:SS)
    val endTime: String?,

    // ========== TIMESTAMP ==========
    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    // ========== RELATIONS ==========
    // Menyertakan data destinasi di dalam item itinerary
    @SerializedName("destination")
    val destination: DestinationResponse?
)