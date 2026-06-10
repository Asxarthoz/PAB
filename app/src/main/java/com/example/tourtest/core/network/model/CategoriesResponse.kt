package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class CategoriesResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
