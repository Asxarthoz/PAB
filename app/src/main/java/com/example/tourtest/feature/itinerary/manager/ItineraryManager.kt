package com.example.tourtest.feature.itinerary.manager

import android.content.Context
import com.example.tourtest.model.Itinerary
import java.util.UUID

object ItineraryManager {
    private const val INTERNAL_FILE_NAME = "dataitinerary.txt"

    fun addItinerary(context: Context, currentUserId: String, destinationId: String, date: String): Boolean {
        return try {
            val newItinerary = Itinerary(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                destinationId = destinationId,
                date = date
            )

            val data = "${newItinerary.id}|${newItinerary.userId}|${newItinerary.destinationId}|${newItinerary.date}\n"

            context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_APPEND).use { outputStream ->
                outputStream.write(data.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun removeItinerary(context: Context, itineraryId: String): Boolean {
        return try {
            val allItinerary = getAllItinerary(context)
            val updatedList = allItinerary.filterNot { it.id == itineraryId }

            context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_PRIVATE).use { outputStream ->
                val stringBuilder = StringBuilder()
                for (item in updatedList) {
                    stringBuilder.append("${item.id}|${item.userId}|${item.destinationId}|${item.date}\n")
                }
                outputStream.write(stringBuilder.toString().toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getAllItinerary(context: Context): List<Itinerary> {
        val itineraryList = mutableListOf<Itinerary>()
        return try {
            val file = context.getFileStreamPath(INTERNAL_FILE_NAME)
            if (file.exists()) {
                val content = context.openFileInput(INTERNAL_FILE_NAME).bufferedReader().use { it.readText() }
                content.lines().forEach { line ->
                    val parts = line.split("|").map { it.trim() }
                    if (parts.size >= 4) {
                        itineraryList.add(
                            Itinerary(
                                id = parts[0],
                                userId = parts[1],
                                destinationId = parts[2],
                                date = parts[3]
                            )
                        )
                    }
                }
            }
            itineraryList
        } catch (e: Exception) {
            e.printStackTrace()
            itineraryList
        }
    }

    fun getItineraryByUser(context: Context, userId: String): List<Itinerary> {
        return getAllItinerary(context).filter { it.userId == userId }
    }
}