package com.example.tourtest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val message: String,
    val destinationId: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
