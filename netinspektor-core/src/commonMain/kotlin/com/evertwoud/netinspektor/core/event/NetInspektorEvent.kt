@file:OptIn(ExperimentalTime::class)

package com.evertwoud.netinspektor.core.event

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
sealed interface NetInspektorEvent {
    val uuid: String
    val timestamp: Long
    val headers: List<String>
    val body: String?

    @Serializable
    data class Request @OptIn(ExperimentalUuidApi::class) constructor(
        override val uuid: String = Uuid.random().toString(),
        override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
        override val headers: List<String>,
        override val body: String?,
        val method: String,
        val url: String,
    ) : NetInspektorEvent

    @Serializable
    data class Response @OptIn(ExperimentalUuidApi::class) constructor(
        override val uuid: String = Uuid.random().toString(),
        override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
        override val headers: List<String>,
        override val body: String?,
        val requestUuid: String?,
        val statusCode: Int,
        val statusDescription: String? = null,
    ) : NetInspektorEvent

    val prettyHeaders get() = headers.joinToString("\n")
}
