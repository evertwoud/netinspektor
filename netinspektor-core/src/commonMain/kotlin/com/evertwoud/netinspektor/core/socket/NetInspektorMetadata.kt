package com.evertwoud.netinspektor.core.socket

import kotlinx.serialization.Serializable

@Serializable
data class NetInspektorMetadata(
    val uuid: String,
    val name: String,
    val platform: String,
)