package com.example.tourtest.feature.notification.viewmodel

import android.app.Application
import com.example.tourtest.feature.notification.manager.NotificationManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.model.NotificationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class NotificationViewModel(
    application: Application,
    private val itineraryManager: ItineraryManager,
    private val notificationManager: NotificationManager,
    private val homepageManager: HomepageManager
): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _notifications = MutableStateFlow<List<NotificationHistory>>(emptyList())
    val notifications = _notifications.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val allItineraries = itineraryManager.getAllItinerary(context)

            val allDestination = homepageManager.readDestinationsFromData(context)

            val generated = allItineraries.mapNotNull { itinerary ->

                val destination = allDestination.find { it.id == itinerary.destinationId }
                if (destination != null) {
                    val message = notificationManager.checkReminderStatus(
                        itineraryDate = itinerary.date,
                        destinationName = destination.name
                    )

                    if (message != null) {
                        NotificationHistory(
                            id = UUID.randomUUID().toString(),
                            message = message,
                            destinationId = itinerary.destinationId,
                            timestamp = System.currentTimeMillis()
                        )
                    } else null
                } else null

            }
            _notifications.value = generated
        }
    }
}