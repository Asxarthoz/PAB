package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("profile_picture_url") val profilePictureUrl: String?,
    @SerializedName("role") val role: String,
    @SerializedName("bisnis_owner") val bisnisOwner: Any?
)