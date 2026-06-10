package com.example.tourtest.feature.notification.viewmodel

import android.content.Context
import com.example.tourtest.feature.notification.manager.NotificationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.notification.dataaccess.NotificationDao
import com.example.tourtest.feature.notification.manager.NotificationHelper
import com.example.tourtest.model.NotificationEntity
import com.example.tourtest.model.NotificationHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSession: UserSession,
    private val notificationDao: NotificationDao,
    private val itineraryManager: ItineraryManager,
    private val notificationManager: NotificationManager,
    private val homepageManager: HomepageManager,
    private val notificationHelper: NotificationHelper
): ViewModel() {
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
        viewModelScope.launch {
            val currentUserId = userSession.userId.firstOrNull() ?: "GUEST"
            if (currentUserId != "GUEST") {
                loadNotifications()
            }
        }
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

                        notificationHelper.showSystemNotification(
                            context = context,
                            notificationId  = uniqueID.hashCode(),
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
