package com.example.tourtest.feature.notification.manager

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object NotificatioManager {

    fun checkReminderStatus(itineraryDate: String, destinationName: String): String? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val eventDate = LocalDate.parse(itineraryDate, formatter).atStartOfDay()
            val now = LocalDateTime.now()

            val diffInDays = ChronoUnit.DAYS.between(now.toLocalDate(), eventDate.toLocalDate())
            val diffInHours = ChronoUnit.HOURS.between(now, eventDate)

            when {
                diffInDays == 7L -> "H-7: Siapkan perlengkapanmu untuk ke $destinationName!"
                diffInDays == 3L -> "H-3: Wah, sebentar lagi kamu berangkat ke $destinationName!"
                diffInDays == 1L -> "H-1: Besok waktunya ke $destinationName! Jangan sampai ada yang tertinggal."
                diffInHours in 1..6 -> "6 Jam Lagi: Siap-siap, perjalananmu ke $destinationName akan segera dimulai!"
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}