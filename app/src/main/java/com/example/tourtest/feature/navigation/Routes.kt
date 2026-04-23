package com.example.tourtest.feature.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Auth : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object EditProfile : Screen()

    @Serializable
    data object ChangePassword : Screen()

    @Serializable
    data object FullScreenImage : Screen()
}