package com.example.tourtest.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes : NavKey {
    @Serializable
    data object AuthRoute : Routes()

    @Serializable
    data object HomeRoute : Routes()

    @Serializable
    data object FavoriteRoute : Routes()

    @Serializable
    data object ItineraryRoute : Routes()

    @Serializable
    data object ProfileRoute : Routes()

    @Serializable
    data object NotificationRoute : Routes()

    @Serializable
    data object EditProfileRoute : Routes()

    @Serializable
    data object ChangePasswordRoute : Routes()

    @Serializable
    data object FullScreenImageRoute : Routes()

    @Serializable
    data class DetailRoute(val destinationId: String) : Routes()
}