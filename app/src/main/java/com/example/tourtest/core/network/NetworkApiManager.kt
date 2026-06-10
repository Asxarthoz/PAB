package com.example.tourtest.core.network

import com.example.tourtest.core.network.model.DestinationResponse
import com.example.tourtest.core.network.model.ReviewResponse
import com.example.tourtest.model.Destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import com.example.tourtest.core.network.toDomainModel
import retrofit2.http.*

// ── 1. INTERFACE RETROFIT (ENDPOINT WEBSITE) ──────────────────────────────────
interface TourizmeApiService {

    @GET("api/destinations")
    suspend fun getAllDestinations(): List<DestinationResponse>

    @GET("api/destinations/{id}")
    suspend fun getDestinationById(@Path("id") id: String): DestinationResponse

    // 💡 Endpoint Kirim Ulasan Baru (POST)
    @POST("api/reviews")
    suspend fun createReview(@Body request: ReviewRequest): Response<ReviewResponse>

    // 💡 Endpoint Edit Ulasan (PUT) -> Menyesuaikan ERD
    @PUT("api/reviews/{id}")
    suspend fun updateReview(@Path("id") reviewId: String, @Body request: ReviewRequest): Response<ReviewResponse>

    // 💡 Endpoint Hapus Ulasan (DELETE) -> Menyesuaikan ERD
    @DELETE("api/reviews/{id}")
    suspend fun deleteReview(@Path("id") reviewId: String): Response<Unit>
}

// Data class pembungkus Body Request untuk dikirim ke Server Website
data class ReviewRequest(
    val destination_id: String,
    val user_id: String,
    val rating: Float,
    val comment: String
)

// ── 2. MANAGER JARINGAN (BAGIAN ISOLASI DATA KODE BARU) ────────────────────────
class NetworkApiManager(private val apiService: TourizmeApiService) {

    // Cache internal khusus jaringan agar tidak bentrok dengan cache lokal biasa
    private var networkCacheDestination: List<Destination>? = null

    /**
     * Mengambil seluruh list destinasi dari API internet, lalu dikonversi ke model domain mobile.
     */
    suspend fun getDestinations(): List<Destination> = withContext(Dispatchers.IO) {
        networkCacheDestination?.let { return@withContext it }
        return@withContext try {
            val remoteData = apiService.getAllDestinations()
            // Memanggil fungsi eksetensi .toDomainModel() dari NetworkMapper kamu
            val domainList = remoteData.map { it.toDomainModel() }
            networkCacheDestination = domainList
            domainList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Mengambil detail satu destinasi beserta array list review-nya berdasarkan ID (String).
     */
    suspend fun getDestinationById(id: String): Destination? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getDestinationById(id)
            response.toDomainModel() // Otomatis dikonversi ke model Destination berekstensi list ulasan lengkap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Mengirimkan aksi tambah ulasan (Review baru) ke server database website kelompokmu.
     */
    suspend fun postReview(destId: String, userId: String, rating: Float, comment: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val requestBody = ReviewRequest(
                destination_id = destId,
                user_id = userId,
                rating = rating,
                comment = comment
            )
            val response = apiService.createReview(requestBody)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Membersihkan cache internal khusus data dari jaringan.
     */
    fun clearCache() {
        networkCacheDestination = null
    }
}