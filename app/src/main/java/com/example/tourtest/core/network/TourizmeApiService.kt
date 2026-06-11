package com.example.tourtest.core.network

import com.example.tourtest.core.network.model.DestinationResponse
import com.example.tourtest.core.network.model.ItineraryResponse
import com.example.tourtest.core.network.model.WishlistResponse
import com.example.tourtest.core.network.model.ReviewResponse
import com.example.tourtest.core.network.model.UserResponse
import com.example.tourtest.core.network.model.ApiResponse
import com.example.tourtest.core.network.model.MeResponse
import com.example.tourtest.core.network.model.PaginatedResponse
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

interface TourizmeApiService {

    // ========== DESTINATION (PUBLIC - No Token) ==========
    @GET("destinations")
    suspend fun getAllDestinations(
        @Query("page") page: Int = 1
    ): PaginatedResponse<DestinationResponse>

    @GET("destinations/search")
    suspend fun searchDestinations(
        @Query("query") query: String
    ): ApiResponse<List<DestinationResponse>>

    // ========== REVIEW (TOURIST - Need Token) ==========
    @GET("tourist/reviews/{destinationId}")
    suspend fun getReviewsByDestinationId(
        @Header("Authorization") token: String,
        @Path("destinationId") destinationId: Long
    ): ApiResponse<List<ReviewResponse>>

    @POST("tourist/reviews/new")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Body request: CreateReviewRequest
    ): ApiResponse<ReviewResponse>

    @PATCH("tourist/reviews/update")
    suspend fun updateReview(
        @Header("Authorization") token: String,
        @Body request: UpdateReviewRequest
    ): ApiResponse<ReviewResponse>

    // ========== WISHLIST (TOURIST - Need Token) ==========
    @GET("tourist/wishlists/")
    suspend fun getWishlist(
        @Header("Authorization") token: String
    ): ApiResponse<List<WishlistResponse>>

    // POST /tourist/wishlists/{id} — sesuai api.php: Route::post('wishlists/{id}', ...)
    @POST("tourist/wishlists/{id}")
    suspend fun addToWishlist(
        @Header("Authorization") token: String,
        @Path("id") destinationId: Long
    ): ApiResponse<Any>

    // DELETE /tourist/wishlists/{id} — sesuai api.php: Route::delete('wishlists/{id}', ...)
    @DELETE("tourist/wishlists/{id}")
    suspend fun removeFromWishlist(
        @Header("Authorization") token: String,
        @Path("id") wishlistId: Long
    ): ApiResponse<Any>

    // ========== ITINERARY (TOURIST - Need Token) ==========
    @GET("tourist/itineraries/")
    suspend fun getItineraries(
        @Header("Authorization") token: String
    ): ApiResponse<List<ItineraryResponse>>

    @POST("tourist/itineraries/new")
    suspend fun createItinerary(
        @Header("Authorization") token: String,
        @Body request: CreateItineraryRequest
    ): ApiResponse<ItineraryResponse>

    @GET("tourist/itineraries/{itinerary}")
    suspend fun getItineraryById(
        @Header("Authorization") token: String,
        @Path("itinerary") itineraryId: Long
    ): ApiResponse<ItineraryResponse>

    @POST("tourist/itineraries/{itinerary}/items")
    suspend fun addItineraryItem(
        @Header("Authorization") token: String,
        @Path("itinerary") itineraryId: Long,
        @Body request: CreateItineraryItemRequest
    ): ApiResponse<Any>

    @DELETE("tourist/itineraries/{itinerary}/items/{itineraryItem}")
    suspend fun deleteItineraryItem(
        @Header("Authorization") token: String,
        @Path("itinerary") itineraryId: Long,
        @Path("itineraryItem") itineraryItemId: Long
    ): ApiResponse<Any>

    @DELETE("tourist/itineraries/{itinerary}")
    suspend fun deleteItinerary(
        @Header("Authorization") token: String,
        @Path("itinerary") itineraryId: Long
    ): ApiResponse<Any>

    // ========== AUTH (Protected - Need Token) ==========
    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): ApiResponse<Any>

    @GET("me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): MeResponse

    // ========== AUTH (Public - No Token) ==========
    @POST("login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterResponse>

    // ========== PROFILE UPDATE (Need Token) ==========
    // PATCH /me — multipart untuk support upload foto profil
    @Multipart
    @PATCH("me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part profile_picture: MultipartBody.Part?
    ): ApiResponse<Any>
}

// ========== DTOs & RESPONSES ==========
data class CreateReviewRequest(
    @SerializedName("destination_id") val destinationId: Long,
    @SerializedName("rating") val rating: Int,
    @SerializedName("description") val description: String
)

data class UpdateReviewRequest(
    @SerializedName("review_id") val reviewId: Long,
    @SerializedName("rating") val rating: Int,
    @SerializedName("description") val description: String
)

data class CreateItineraryRequest(
    @SerializedName("title") val title: String,
    @SerializedName("start_date") val startDate: String
)

data class CreateItineraryItemRequest(
    @SerializedName("destination_id") val destinationId: Long,
    @SerializedName("day") val day: Int,
    @SerializedName("sequence_order") val sequenceOrder: Int,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String
)

data class LoginRequest(
    @SerializedName("identity") val identity: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class RegisterRequest(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
    @SerializedName("role") val role: String
)

data class RegisterResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String
)