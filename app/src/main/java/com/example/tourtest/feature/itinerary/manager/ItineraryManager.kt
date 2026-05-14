package com.example.tourtest.feature.itinerary.manager

import android.content.Context
import com.example.tourtest.model.Itinerary
import java.util.UUID

object ItineraryManager {
    private const val INTERNAL_FILE_NAME = "dataitinerary.txt"

    fun addDestination(context: Context, currentUserId: String, destinationId: String, date: String): Boolean {
        return try {
            val allItinerary = getAllItinerary(context).toMutableList()

            if (currentUserId.isBlank()) {
                android.util.Log.e("ITINERARY_DEBUG", "ERROR: currentUserId is BLANK!")
                return false
            }

            val existingIndex = allItinerary.indexOfFirst {
                it.userId == currentUserId && it.destinationId == destinationId
            }

            if (existingIndex != -1) {
                val existingItem = allItinerary[existingIndex]
                allItinerary[existingIndex] = existingItem.copy(date = date)
            } else {
                allItinerary.add(
                    Itinerary(
                        id = UUID.randomUUID().toString(),
                        userId = currentUserId,
                        destinationId = destinationId,
                        date = date
                    )
                )
            }

            saveAll(context, allItinerary)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun removeDestination(context: Context, itineraryId: String): Boolean {
        return try {
            val allItinerary = getAllItinerary(context)
            val updatedList = allItinerary.filterNot { it.id == itineraryId }

            saveAll(context, updatedList)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getAllItinerary(context: Context): List<Itinerary> {
        val itineraryList = mutableListOf<Itinerary>()
        val file = context.getFileStreamPath(INTERNAL_FILE_NAME)

        if (!file.exists()) return  itineraryList
        return try {
            context.openFileInput(INTERNAL_FILE_NAME).bufferedReader().useLines { lines ->
                lines.forEach { line ->
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

    private fun saveAll(context: Context, list: List<Itinerary>) {
        context.openFileOutput(INTERNAL_FILE_NAME, Context.MODE_PRIVATE).use { output ->
            val data = list.joinToString("") { item ->
                "${item.id}|${item.userId}|${item.destinationId}|${item.date}\n"
            }
            output.write(data.toByteArray())
        }
    }

    fun getItineraryByUser(context: Context, userId: String): List<Itinerary> {
        return getAllItinerary(context).filter { it.userId == userId }
    }
}