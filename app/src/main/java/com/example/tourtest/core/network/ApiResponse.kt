package com.example.tourtest.core.network.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?  // nullable — API error kadang return [] atau null
)

// Wrapper khusus untuk response /me yang formatnya {"data": {"0": {...}, "profile_url :": null}}
// Kita ignore dan parse manual di NetworkApiManager
data class MeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Map<String, Any?>?  // parse sebagai Map dulu
)

// Wrapper untuk response destinations yang berpaginasi
data class PaginatedResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<T>?,
    @SerializedName("meta") val meta: PaginationMeta?
)

data class PaginationMeta(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int
)