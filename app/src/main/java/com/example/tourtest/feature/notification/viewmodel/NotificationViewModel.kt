package com.example.tourtest.feature.notification.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tourtest.feature.notification.manager.NotificationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.notification.dataaccess.NotificationDao
import com.example.tourtest.model.NotificationEntity
import com.example.tourtest.model.NotificationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class NotificationViewModel(
    application: Application,
    private val userId: String,
    private val notificationDao: NotificationDao,
    private val itineraryManager: ItineraryManager,
    private val notificationManager: NotificationManager,
    private val homepageManager: HomepageManager
): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val CHANNEL_ID = "tourizme_reminders"

    val notification: StateFlow<List<NotificationHistory>> = notificationDao.getNotificationsByUser(userId)
        .map { entities ->
            entities.map { entity ->
                NotificationHistory(
                    id = entity.id,
                    message = entity.message,
                    destinationId = entity.destinationId,
                    timestamp = entity.timestamp,
                    isRead = entity.isRead
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        createNotificationChannel()
        if (userId != "GUEST") {
            loadNotifications()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TourIzme Reminders"
            val descriptionText = "Notifications for trip reminders"
            val importance = AndroidNotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: AndroidNotificationManager =
                context.getSystemService(AndroidNotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showSystemNotification(id: Int, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with app icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager: AndroidNotificationManager =
            context.getSystemService(AndroidNotificationManager::class.java)
        notificationManager.notify(id, builder.build())
    }

    fun loadNotifications() {
        if (userId == "GUEST") return

        viewModelScope.launch {
            val allItineraries = itineraryManager.getItineraryByUser(context, userId)
            val allDestination = homepageManager.readDestinationsFromData(context)

            allItineraries.forEach { itinerary ->
                val destination = allDestination.find { it.id == itinerary.destinationId }
                if (destination != null) {
                    val message = notificationManager.checkReminderStatus(
                        itineraryDate = itinerary.date,
                        destinationName = destination.name
                    )

                    if (message != null) {
                        // Unique ID based on user, destination and message content to avoid duplicates
                        val reminderKey = message.split(":")[0] // e.g., "H-7", "6 Jam Lagi"
                        val uniqueID = "${userId}_${itinerary.destinationId}_$reminderKey"
                        
                        val entity = NotificationEntity(
                            id = uniqueID,
                            userId = userId,
                            message = message,
                            destinationId = itinerary.destinationId,
                            timestamp = System.currentTimeMillis()
                        )
                        
                        // Check if already notified to avoid spamming system notifications
                        // In a real app, we'd check if this specific reminder was already sent
                        notificationDao.insertNotification(entity)
                        
                        // Optional: Trigger system notification if it's new
                        // (Simplified logic for this project context)
                        showSystemNotification(
                            id = uniqueID.hashCode(),
                            title = "Pengingat Perjalanan",
                            message = message
                        )
                    }
                }
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationDao.markAsRead(id)
        }
    }
}
