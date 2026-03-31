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
    val headers: Map<String, String>
    val body: Body?

    @Serializable
    data class Request @OptIn(ExperimentalUuidApi::class) constructor(
        override val uuid: String = Uuid.random().toString(),
        override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
        override val headers: Map<String, String>,
        override val body: Body?,
        val method: String,
        val url: String,
    ) : NetInspektorEvent

    @Serializable
    data class Response @OptIn(ExperimentalUuidApi::class) constructor(
        override val uuid: String = Uuid.random().toString(),
        override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
        override val headers: Map<String, String>,
        override val body: Body?,
        val requestUuid: String?,
        val statusCode: Int,
        val statusDescription: String? = null,
    ) : NetInspektorEvent

    @Serializable
    data class Body(
        val contentType: String?,
        val data: ByteArray?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Body

            if (contentType != other.contentType) return false
            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = contentType.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    val prettyHeaders
        get() = headers.map { (key, value) -> "$key: $value" }.joinToString("\n")
}
