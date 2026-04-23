package com.example.tourtest.feature.homepage.manager

import android.content.Context
import com.example.tourtest.model.Destination
import kotlin.sequences.forEach

object HomepageManager {
    public fun readDestinationsFromData(context: Context): List<Destination> {
        val list = mutableListOf<Destination>()
        try {
            context.assets.open("datawisata.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val p = line.split("|")
                    if (p.size == 7) list.add(Destination(p[0], p[1], p[2], p[3], p[4], p[5], p[6]))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    public fun getDestinationById(context: Context, id: String): Destination? {
        val destinationList = readDestinationsFromData(context)
        return destinationList.find { it.id == id }

    }
}