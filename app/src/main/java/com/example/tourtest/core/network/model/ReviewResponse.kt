package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long?,  // Nullable karena tidak selalu ada di response
    @SerializedName("username") val username: String,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("description") val description: String,
    @SerializedName("owner_reply") val ownerReply: String?
)