package com.example.tourtest.core.network

import com.example.tourtest.core.network.model.*
import com.example.tourtest.model.*
import java.util.Locale

fun DestinationResponse.toDomainModel(): Destination {
    // 1. Map list review dari server menggunakan mapper khusus ReviewResponse
    val mappedReviews = this.reviews?.map { it.toDomainModel() } ?: emptyList()

    // 2. Perhitungan rata-rata rating yang presisi mengikuti standar HomepageManager.kt kamu
    val finalAvgRating = this.rating ?: if (mappedReviews.isNotEmpty()) {
        val validRatings = mappedReviews.filter { it.ratingGiven > 0f }
        if (validRatings.isNotEmpty()) {
            val total = validRatings.sumOf { it.ratingGiven.toDouble() }
            String.format(Locale.US, "%.1f", total / validRatings.size).toFloat()
        } else 0f
    } else 0f

    return Destination(
        id = this.id.trim(),               // Menggunakan id String langsung dari DestinationResponse kamu
        name = this.name,
        location = this.location,
        price = this.price,                // Menyesuaikan price String bawaan response kamu
        imageUrl = this.image,             // Menyesuaikan @SerializedName("image") dari file kamu
        gmapUrl = this.gmaps,              // Menyesuaikan @SerializedName("gmaps") dari file kamu
        description = this.description,
        averageRating = finalAvgRating,
        reviews = mappedReviews,
        // Jam operasional lokal tetap dipertahankan aman sebagai fallback default
        openTime = "00:00",
        closeTime = "00:00"
    )
}

fun ReviewResponse.toDomainModel(): Review {
    return Review(
        userId = this.userId.trim(),
        // Mengambil nama dari nested object user (UsersResponse) jika tersedia dari server
        userName = this.user?.name ?: "Wisatawan Tourizme",
        ratingGiven = this.rating,
        comment = this.comment             // DISESUAIKAN: menggunakan .comment sesuai isi file ReviewResponse.kt kamu
    )
}

fun UsersResponse.toDomainModel(): Users {
    return Users(
        id = this.id.trim(),
        name = this.name,
        nickName = this.name.substringBefore(" "), // Ekstrak nama panggilan secara otomatis
        email = this.email,
        password = "",                     // Keamanan: kosongkan password di sisi client
        role = "wisatawan",
        isVerified = false,
        profileImage = this.image          // Mengambil path foto profil dari properti 'image' kamu
    )
}