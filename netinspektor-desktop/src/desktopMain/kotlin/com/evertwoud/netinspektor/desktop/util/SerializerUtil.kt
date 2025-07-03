package com.evertwoud.netinspektor.desktop.util

import kotlinx.serialization.json.Json

internal object SerializerUtil {
    val json = Json {
        prettyPrint = false
    }
}