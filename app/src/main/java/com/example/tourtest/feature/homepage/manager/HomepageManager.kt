package com.example.tourtest.feature.homepage.manager

import android.content.Context
import com.example.tourtest.model.Destination
import com.example.tourtest.model.Review

object HomepageManager {

    private var cacheDestination: List<Destination>? = null

    private fun readAllReviews(context: Context): List<Pair<String, Review>> {
        val allReviews = mutableListOf<Pair<String, Review>>()

        try {
            context.assets.open("reviews.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty()) return@forEach
                    val p = trimmed.split("|")
                    when (p.size) {
                        5 -> allReviews.add(p[0] to Review(p[1], p[2], p[3].toFloatOrNull() ?: 0f, p[4]))
                        4 -> allReviews.add(p[0] to Review("GUEST", p[1], p[2].toFloatOrNull() ?: 0f, p[3]))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val internalFile = context.getFileStreamPath("user_reviews.txt")
            if (internalFile != null && internalFile.exists()) {
                context.openFileInput("user_reviews.txt").bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        val trimmed = line.trim()
                        if (trimmed.isEmpty()) return@forEach
                        val p = trimmed.split("|")
                        if (p.size == 5) {
                            allReviews.add(p[0] to Review(p[1], p[2], p[3].toFloatOrNull() ?: 0f, p[4]))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return allReviews
    }

    fun readDestinationsFromData(context: Context): List<Destination> {
        cacheDestination?.let { return it }

        val list = mutableListOf<Destination>()
        try {
            val allReviewPairs = readAllReviews(context)

            context.assets.open("datawisata.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val p = line.trim().split("|")
                    if (p.size == 9) {
                        val destId = p[0]
                        val filteredReviews = allReviewPairs
                            .filter { it.first == destId }
                            .map { it.second }

                        val validRatings = filteredReviews.filter { it.ratingGiven > 0f }
                        val avgRating = if (validRatings.isNotEmpty()) {
                            String.format(
                                java.util.Locale.US,
                                "%.1f",
                                validRatings.sumOf { it.ratingGiven.toDouble() } / validRatings.size
                            ).toFloat()
                        } else 0f

                        list.add(
                            Destination(
                                id          = p[0],
                                name        = p[1],
                                location    = p[2],
                                price       = p[3],
                                imageUrl    = p[4],
                                gmapUrl     = p[5],
                                description = p[6],
                                averageRating = avgRating,
                                reviews     = filteredReviews,
                                openTime    = p[7],
                                closeTime   = p[8]
                            )
                        )
                    }
                }
            }
            cacheDestination = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun getDestinationById(context: Context, id: String): Destination? =
        readDestinationsFromData(context).find { it.id == id }

    fun clearCache() {
        cacheDestination = null
    }


    fun getUserReview(context: Context, destId: String, userId: String): Review? {
        return try {
            val internalFile = context.getFileStreamPath("user_reviews.txt")
            if (internalFile == null || !internalFile.exists()) return null

            context.openFileInput("user_reviews.txt").bufferedReader().useLines { lines ->
                lines.mapNotNull { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty()) return@mapNotNull null
                    val p = trimmed.split("|")
                    if (p.size == 5 && p[0] == destId && p[1] == userId) {
                        Review(p[1], p[2], p[3].toFloatOrNull() ?: 0f, p[4])
                    } else null
                }.firstOrNull()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun addReview(
        context: Context,
        destId: String,
        userId: String,
        userName: String,
        rating: Float,
        comment: String
    ): Boolean {
        if (getUserReview(context, destId, userId) != null) return false

        return try {
            val line = "$destId|$userId|$userName|$rating|$comment\n"
            context.openFileOutput("user_reviews.txt", Context.MODE_APPEND)
                .use { it.write(line.toByteArray()) }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun updateReview(
        context: Context,
        destId: String,
        userId: String,
        userName: String,
        newRating: Float,
        newComment: String
    ): Boolean {
        return try {
            val internalFile = context.getFileStreamPath("user_reviews.txt")
            val linesToKeep = mutableListOf<String>()

            if (internalFile != null && internalFile.exists()) {
                context.openFileInput("user_reviews.txt").bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        val trimmed = line.trim()
                        if (trimmed.isEmpty()) return@forEach
                        val p = trimmed.split("|")
                        // Hapus baris lama milik user ini untuk destinasi ini
                        if (p.size == 5 && p[0] == destId && p[1] == userId) return@forEach
                        linesToKeep.add(trimmed)
                    }
                }
            }

            // Tulis baris baru
            linesToKeep.add("$destId|$userId|$userName|$newRating|$newComment")

            context.openFileOutput("user_reviews.txt", Context.MODE_PRIVATE).use { output ->
                linesToKeep.forEach { output.write(("$it\n").toByteArray()) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteReview(
        context: Context,
        destId: String,
        userId: String
    ): Boolean {
        return try {
            val internalFile = context.getFileStreamPath("user_reviews.txt")
            val linesToKeep = mutableListOf<String>()

            if (internalFile != null && internalFile.exists()) {
                context.openFileInput("user_reviews.txt").bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        val trimmed = line.trim()
                        if (trimmed.isEmpty()) return@forEach
                        val p = trimmed.split("|")
                        if (p.size == 5 && p[0] == destId && p[1] == userId) return@forEach
                        linesToKeep.add(trimmed)
                    }
                }
            }

            context.openFileOutput("user_reviews.txt", Context.MODE_PRIVATE).use { output ->
                linesToKeep.forEach { output.write(("$it\n").toByteArray()) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}