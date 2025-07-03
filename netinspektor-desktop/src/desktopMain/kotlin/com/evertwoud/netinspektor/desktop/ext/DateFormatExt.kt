package com.evertwoud.netinspektor.desktop.ext

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Long.formatAsTime() = Instant.fromEpochMilliseconds(this).let { instant ->
    LocalDateTime.Format {
        hour()
        char(':')
        minute()
        char(':')
        second()
    }.format(
        instant.toLocalDateTime(TimeZone.currentSystemDefault())
    )
}