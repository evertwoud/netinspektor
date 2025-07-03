package com.evertwoud.netinspektor.core.socket

import kotlinx.serialization.Serializable

@Serializable
data class NetInspektorSocketMessage<T>(
    val type: String,
    val data: T,
)