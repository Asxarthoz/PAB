package com.example.tourtest.feature.notification.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationHelper {
    private const val CHANNEL_ID = "tourizme_reminders"
    private const val CHANNEL_NAME = "Pengigat Perjalanan"

    fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Saluran untuk pengigta rencan perjalanan"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}