package com.evertwoud.netinspektor.core.socket

import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import kotlinx.serialization.Serializable

@Serializable
data class NetInspektorSessionHistory(
    val requests: List<NetInspektorEvent.Request>,
    val responses: List<NetInspektorEvent.Response>,
)