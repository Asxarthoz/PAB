package com.example.tourtest.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    @Serializable
    data object AuthRoute : NavKey

    @Serializable
    data object HomeRoute : NavKey

    @Serializable
    data object ProfileRoute : NavKey

    @Serializable
    data object EditProfileRoute : NavKey

    @Serializable
    data object ChangePasswordRoute : NavKey

    @Serializable
    data object FullScreenImageRoute : NavKey
}