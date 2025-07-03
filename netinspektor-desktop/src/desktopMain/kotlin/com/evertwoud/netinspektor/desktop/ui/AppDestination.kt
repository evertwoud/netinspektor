package com.evertwoud.netinspektor.desktop.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {
    @Serializable
    data object Events : AppDestination()
}