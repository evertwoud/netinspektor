package com.evertwoud.netinspektor.core.socket

import kotlinx.serialization.Serializable

@Serializable
data class NetInspektorDevice(
    val sessionName: String,
    val platform: String,
    val host: String,
    val port: Int,
)