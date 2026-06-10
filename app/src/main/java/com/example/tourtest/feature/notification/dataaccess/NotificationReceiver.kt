package com.example.tourtest.feature.notification.dataaccess

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tourtest.feature.notification.manager.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("NOTIFICATIOn_MESSAGE") ?: "Ada rencana perjalanan untuk anda hari ini!"
        val destinationId = intent?.getStringExtra("DESTINATION_ID") ?: ""

        context?.let {
            notificationHelper.showSystemNotification(
                context = it,
                title = "Tourizme Notification",
                message = message,
                notificationId = destinationId.hashCode()
            )
        }
    }
}