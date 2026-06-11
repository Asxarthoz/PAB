package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class CategoriesResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)