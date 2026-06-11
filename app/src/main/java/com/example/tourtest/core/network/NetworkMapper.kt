package com.example.tourtest.core.network

import com.example.tourtest.core.network.model.DestinationResponse
import com.example.tourtest.core.network.model.ReviewResponse
import com.example.tourtest.core.network.model.WishlistResponse
import com.example.tourtest.core.network.model.UserResponse // 👈 PERBAIKAN: Import UserResponse yang benar
import com.example.tourtest.model.Destination
import com.example.tourtest.model.Review
import com.example.tourtest.model.Users
import com.example.tourtest.model.Favorite


fun DestinationResponse.toDomainModel(): Destination {
    val imageUrl = this.thumbnail.orEmpty()

    // Parse ISO timestamp ke format HH:mm, contoh: "2026-06-11T09:00:00.000000Z" → "09:00"
    fun parseTime(raw: String): String {
        return try {
            raw.substring(11, 16)  // ambil "HH:mm" dari "YYYY-MM-DDTHH:MM:SS.000000Z"
        } catch (e: Exception) {
            raw
        }
    }

    return Destination(
        id = this.id.toString(),
        name = this.name,
        location = this.location,
        price = this.price.toString(),
        imageUrl = imageUrl,
        gmapUrl = "",
        description = this.description,
        averageRating = this.averageRating?.toFloat() ?: 0f,
        reviews = this.reviews?.map { it.toDomainModel() } ?: emptyList(),
        openTime = parseTime(this.openTime),
        closeTime = parseTime(this.closeTime)
    )
}

// 2. PEMETAAN REVIEW
fun ReviewResponse.toDomainModel(): Review {
    return Review(
        // Gunakan userId jika ada, fallback ke username agar checkUserReviewStatus bisa cocok
        userId = this.userId?.toString() ?: this.username,
        userName = this.username,
        ratingGiven = this.rating.toFloat(),
        comment = this.description
    )
}

// 3. PEMETAAN USER / PROFILE
fun UserResponse.toDomainModel(): Users {
    return Users(
        id = this.id.toString(),
        name = this.fullname,
        nickName = this.username,
        email = this.email,
        password = "",
        role = this.role,
        isVerified = false,
        profileImage = this.profilePicture ?: ""
    )
}

// 4. PEMETAAN WISHLIST
fun WishlistResponse.toDomainModel(): Favorite {
    return Favorite(
        id = this.id.toString(),
        userId = "",
        destinationId = this.destinationId.toString()
    )
}