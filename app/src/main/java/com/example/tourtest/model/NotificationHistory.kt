package com.example.tourtest.model

import android.media.AudioTimestamp

data class NotificationHistory(
    val id: String,
    val message: String,
    val destinationId: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
