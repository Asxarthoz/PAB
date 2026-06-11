package com.example.tourtest.core.network

import com.example.tourtest.core.network.model.ItineraryResponse
import com.example.tourtest.core.network.model.UserResponse
import com.example.tourtest.core.network.model.WishlistResponse
import com.example.tourtest.model.Destination
import com.example.tourtest.core.network.toDomainModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkApiManager @Inject constructor(
    private val apiService: TourizmeApiService
) {
    private val gson = Gson()

    // Cache semua destinasi agar concurrent calls tidak load ulang
    private var destinationCache: List<Destination>? = null
    private var isLoadingDestinations = false
    private val destinationMutex = kotlinx.coroutines.sync.Mutex()

    // ========== DESTINATION (Public - No Token) ==========
    suspend fun getDestinations(): List<Destination> = withContext(Dispatchers.IO) {
        // Return cache jika sudah ada
        destinationCache?.let { return@withContext it }

        destinationMutex.withLock {
            // Double-check setelah dapat lock
            destinationCache?.let { return@withLock it }

            val allDestinations = mutableListOf<Destination>()
            var page = 1
            var lastPage = 1

            do {
                val response = apiService.getAllDestinations(page)
                if (response.success) {
                    val items = response.data?.map { it.toDomainModel() } ?: emptyList()
                    allDestinations.addAll(items)
                    lastPage = response.meta?.lastPage ?: 1
                    println("📄 Loaded page $page/$lastPage — ${items.size} items")
                } else {
                    println("API Error page $page: ${response.message}")
                    break
                }
                page++
            } while (page <= lastPage)

            println("✅ Total destinations loaded: ${allDestinations.size}")
            destinationCache = allDestinations
            allDestinations
        }
    }

    // Cari dari cache, atau load semua dulu baru filter
    suspend fun getDestinationById(id: Long): Destination? = withContext(Dispatchers.IO) {
        return@withContext try {
            destinationCache?.find { it.id == id.toString() }
                ?: run {
                    val list = getDestinations()
                    list.find { it.id == id.toString() }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========== PROFILE (Need Token) ==========
    // /me response format: {"data": {"0": {user_obj}, "profile_url :": null}}
    // Perlu parse manual karena format tidak standar
    suspend fun getCurrentUser(token: String): UserResponse? = withContext(Dispatchers.IO) {
        println("🔍 getCurrentUser() - TOKEN: ${token.take(30)}...")
        return@withContext try {
            val response = apiService.getCurrentUser("Bearer $token")
            if (!response.success || response.data == null) {
                println("❌ API Error: ${response.message}")
                return@withContext null
            }

            // data berupa Map<String, Any?> — ambil entry "0" yang berisi user object
            val userMap = response.data["0"] as? Map<*, *>
            if (userMap == null) {
                println("❌ User data tidak ditemukan di key '0'")
                return@withContext null
            }

            // Konversi Map ke UserResponse via Gson
            val userJson = gson.toJson(userMap)
            val userResponse = gson.fromJson(userJson, UserResponse::class.java)
            println("✅ USER DATA: ${userResponse.username}")
            userResponse
        } catch (e: Exception) {
            println("❌ EXCEPTION getCurrentUser: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // PATCH /me — field "name" sesuai API, bukan "fullname"
    suspend fun updateProfile(
        token: String,
        name: String? = null,
        email: String? = null,
        password: String? = null,
        imageFile: File? = null
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val nameBody = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email?.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageBody = imageFile?.let {
                val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", it.name, requestFile)
            }

            val response = apiService.updateProfile(
                token = "Bearer $token",
                name = nameBody,
                email = emailBody,
                password = passwordBody,
                profile_picture = imageBody
            )
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ========== REVIEW (Need Token) ==========
    suspend fun createReview(
        token: String,
        destinationId: Long,
        rating: Int,
        description: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = CreateReviewRequest(
                destinationId = destinationId,
                rating = rating,
                description = description
            )
            val response = apiService.createReview("Bearer $token", request)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ========== WISHLIST (Need Token) ==========
    suspend fun getWishlist(token: String): List<WishlistResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getWishlist("Bearer $token")
            if (response.success) response.data ?: emptyList() else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addToWishlist(token: String, destinationId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.addToWishlist("Bearer $token", destinationId)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun removeFromWishlist(token: String, wishlistId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.removeFromWishlist("Bearer $token", wishlistId)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ========== ITINERARY (Need Token) ==========
    suspend fun getItineraries(token: String): List<ItineraryResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getItineraries("Bearer $token")
            if (response.success) response.data ?: emptyList() else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createItinerary(token: String, title: String, startDate: String): ItineraryResponse? = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = CreateItineraryRequest(title, startDate)
            val response = apiService.createItinerary("Bearer $token", request)
            if (response.success) response.data else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteItinerary(token: String, itineraryId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.deleteItinerary("Bearer $token", itineraryId)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addItineraryItem(
        token: String,
        itineraryId: Long,
        destinationId: Long,
        day: Int = 1,
        sequenceOrder: Int = 1,
        startTime: String = "08:00",
        endTime: String = "10:00"
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = CreateItineraryItemRequest(
                destinationId = destinationId,
                day = day,
                sequenceOrder = sequenceOrder,
                startTime = startTime,
                endTime = endTime
            )
            val response = apiService.addItineraryItem("Bearer $token", itineraryId, request)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteItineraryItem(
        token: String,
        itineraryId: Long,
        itineraryItemId: Long
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.deleteItineraryItem("Bearer $token", itineraryId, itineraryItemId)
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ========== AUTH ==========
    suspend fun login(identity: String, password: String): LoginResponse? = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = LoginRequest(identity, password)
            val response = apiService.login(request)
            if (response.success) response.data else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun register(
        fullname: String,
        username: String,
        email: String,
        password: String,
        role: String = "tourist"
    ): RegisterResponse? = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RegisterRequest(
                fullname = fullname,
                username = username,
                email = email,
                password = password,
                passwordConfirmation = password,  // sama dengan password
                role = role
            )
            val response = apiService.register(request)
            if (response.success) response.data else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearCache() {
        destinationCache = null
    }
}
