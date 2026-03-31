package com.evertwoud.netinspektor.desktop.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

data object JsonBodyFormatter {

    @OptIn(ExperimentalSerializationApi::class)
    fun prettyPrint(input: String?): String? {
        if (input == null) return null
        // Setup Json configuration for pretty print
        val json = Json {
            prettyPrint = true
            prettyPrintIndent = FormatConstants.INDENT
        }
        return try {
            val element = json.decodeFromString<JsonElement?>(input)
            val formatted = json.encodeToString(element)
            formatted
        } catch (e: SerializationException) {
            input
        }
    }

    fun minified(input: String?): String? = try {
        if (input == null) return null
        // Setup Json configuration for pretty print
        val json = Json {
            prettyPrint = false
        }
        // Attempt parsing content
        val element = json.decodeFromString<JsonElement?>(input)
        val formatted = json.encodeToString(element)
        formatted
    } catch (e: SerializationException) {
        input
    }
}