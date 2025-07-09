package com.evertwoud.netinspektor.desktop.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

data object BodyFormatter {

    @OptIn(ExperimentalSerializationApi::class)
    fun prettyPrint(input: Any?): String? {
        // Setup Json configuration for pretty print
        val json = Json {
            prettyPrint = true
            prettyPrintIndent = FormatConstants.INDENT
        }
        return when (input) {
            is String -> try {
                val element = json.decodeFromString<JsonElement?>(input)
                val formatted = json.encodeToString(element)
                formatted
            } catch (e: SerializationException) {
                input
            }

            null -> null
            else -> input.toString()
        }
    }

    fun minified(input: Any?): String? = when (input) {
        is String -> try {
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

        null -> null
        else -> input.toString()
    }
}