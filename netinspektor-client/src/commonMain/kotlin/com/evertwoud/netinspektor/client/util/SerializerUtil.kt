package com.evertwoud.netinspektor.client.util

import kotlinx.serialization.json.Json

internal object SerializerUtil {
    val json = Json {
        prettyPrint = false
    }
}