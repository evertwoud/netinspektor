package com.evertwoud.netinspektor.desktop.ext

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
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

@OptIn(ExperimentalTime::class)
fun Long.formatAsTimeStamp() = Instant.fromEpochMilliseconds(this).let { instant ->
    LocalDateTime.Format {
        day()
        char('-')
        monthNumber()
        char('-')
        year()
        char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()
        char('.')
        secondFraction()
    }.format(
        instant.toLocalDateTime(TimeZone.currentSystemDefault())
    )
}

@OptIn(ExperimentalTime::class)
fun Long.formatAsDuration() = this.milliseconds.toString()