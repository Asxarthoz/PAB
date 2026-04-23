package com.example.tourtest.feature.wishlist.manager

import android.content.Context
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.model.Users
import com.example.tourtest.model.WishList

object WishlistManager {
    private const val INTERNAL_FILE_NAME = "datawishlist.txt"
    private const val ASSETS_FILE_NAME = "datawishlist.txt"

    public fun addDestination(context: Context, currentUserId : String, destinationId: String) : Boolean {
        return try {
            if (isDestinationExists(context, currentUserId, destinationId)) {
                return false
            }

            val newWishList = WishList(
                id = java.util.UUID.randomUUID().toString(),
                userId = currentUserId,
                destinationId = destinationId
            )

            val data = "${newWishList.id}|${newWishList.userId}|${newWishList.destinationId}\n"

            context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_APPEND).use { outputStream ->
                outputStream.write(data.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    public fun removeDestination(context: Context, currentUserId : String, destinationId: String): Boolean {
        return try {
            val wishLists = getAllWish(context)
            val updatedWish = wishLists.filterNot { it.userId == currentUserId && it.destinationId == destinationId }

            context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_PRIVATE).use { outputStream ->
                val stringBuilder = StringBuilder()
                for (wish in updatedWish) {
                    stringBuilder.append("${wish.id}|${wish.userId}|${wish.destinationId}\n")
                }
                outputStream.write(stringBuilder.toString().toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getAllWish(context: Context): List<WishList> {
        val wishLists = mutableListOf<WishList>()
        return try {
            val file = context.getFileStreamPath(INTERNAL_FILE_NAME)
            if (file.exists()) {
                val content = context.openFileInput(INTERNAL_FILE_NAME).bufferedReader().use { it.readText() }
                val lines = content.lines()
                for (line in lines) {
                    if (line.isNotBlank()) {
                        val trimmedLine = line.trim()
                        if(trimmedLine.isNotBlank()) {
                            val parts = trimmedLine.split("|").map { it.trim() }
                            if (parts.size >= 3) {
                                wishLists.add(
                                    WishList(
                                        id = parts[0],
                                        userId = parts[1],
                                        destinationId = parts[2]
                                    )
                                )
                            }
                        }
                    }
                }
            }
            wishLists
        } catch (e: Exception) {
            e.printStackTrace()
            wishLists
        }
    }

    fun isDestinationExists(context: Context, currentUserId: String, destinationId: String): Boolean {
        val wishLists = getAllWish(context)
        return wishLists.any { it.userId == currentUserId && it.destinationId == destinationId }
    }
}