package com.example.tourtest.feature.notification.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tourtest.feature.notification.manager.NotificationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.notification.dataaccess.NotificationDao
import com.example.tourtest.model.NotificationEntity
import com.example.tourtest.model.NotificationHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    application: Application,
    private val userSession: UserSession,
    private val notificationDao: NotificationDao,
    private val itineraryManager: ItineraryManager,
    private val notificationManager: NotificationManager,
    private val homepageManager: HomepageManager
): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val CHANNEL_ID = "tourizme_reminders"

    private val userIdFlow = userSession.userId

    val notification: StateFlow<List<NotificationHistory>> = userIdFlow.flatMapLatest { uid ->
        notificationDao.getNotificationsByUser(uid ?: "GUEST")
    }.map { entities ->
        entities.map { entity ->
            NotificationHistory(
                id = entity.id,
                message = entity.message,
                destinationId = entity.destinationId,
                timestamp = entity.timestamp,
                isRead = entity.isRead
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        createNotificationChannel()
        viewModelScope.launch {
            val currentUserId = userSession.userId.firstOrNull() ?: "GUEST"
            if (currentUserId != "GUEST") {
                loadNotifications()
            }
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

        viewModelScope.launch {
            val currentUserId = userSession.userId.firstOrNull() ?: "GUEST"
            if (currentUserId == "GUEST") return@launch

            val allItineraries = itineraryManager.getItineraryByUser(context, currentUserId)
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
                        val uniqueID = "${currentUserId}_${itinerary.destinationId}_$reminderKey"
                        
                        val entity = NotificationEntity(
                            id = uniqueID,
                            userId = currentUserId,
                            message = message,
                            destinationId = itinerary.destinationId,
                            timestamp = System.currentTimeMillis()
                        )
                        

                        notificationDao.insertNotification(entity)

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
