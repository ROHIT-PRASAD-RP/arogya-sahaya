package com.example.arogyasahaya3.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Splash : Destination
    @Serializable
    data object Login : Destination
    @Serializable
    data object Register : Destination
    @Serializable
    data object ForgotPassword : Destination
    @Serializable
    data object Home : Destination
    @Serializable
    data class Profile(val uid: String) : Destination
    @Serializable
    data object PillReminder : Destination
    @Serializable
    data object Vitals : Destination
    @Serializable
    data object SOS : Destination
    @Serializable
    data object AshaConnect : Destination
}
