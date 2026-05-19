package com.example.tourtest.feature.notification.dataaccess

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tourtest.feature.notification.manager.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("NOTIFICATIOn_MESSAGE") ?: "Ada rencana perjalanan untuk anda hari ini!"
        val destinationId = intent?.getStringExtra("DESTINATION_ID") ?: ""

        context?.let {
            NotificationHelper.showSystemNotification(
                context = it,
                title = "Tourizme Notification",
                message = message,
                notificationId = destinationId.hashCode()
            )
        }
    }
}