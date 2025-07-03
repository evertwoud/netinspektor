package com.evertwoud.netinspektor.core.socket

import kotlinx.serialization.Serializable

@Serializable
data class NetInspektorInitializeMessage(val uuid: String, )