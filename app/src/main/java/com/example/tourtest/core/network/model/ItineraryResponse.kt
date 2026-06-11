package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ItineraryResponse(
    // ========== PRIMARY ==========
    @SerializedName("id")
    val id: Long,  // 🔄 String → Long

    @SerializedName("user_id")
    val userId: Long,  // 🔄 String → Long

    // ========== ITINERARY INFO ==========
    @SerializedName("title")  // ✅ TAMBAH: judul itinerary
    val title: String,

    @SerializedName("start_date")
    val startDate: String,  // format: YYYY-MM-DD

    @SerializedName("estimated_price")  // ✅ TAMBAH: estimasi total harga
    val estimatedPrice: Double?,

    // ========== TIMESTAMP ==========
    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    // ========== RELATIONS ==========
    @SerializedName("itinerary_items")
    val items: List<ItineraryItemResponse>? = emptyList()
)