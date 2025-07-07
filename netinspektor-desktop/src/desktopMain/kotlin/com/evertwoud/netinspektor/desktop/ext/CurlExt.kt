package com.evertwoud.netinspektor.desktop.ext

import com.evertwoud.netinspektor.core.event.NetInspektorEvent

fun NetInspektorEvent.Request.toCurlRequest() = buildString {
    // Start with method
    append("curl -X ${method.uppercase()}")
    // Append url
    append("\\\n")
    append("  '$url'")
    // Add headers
    headers.forEach {
        append("\\\n")
        append("  -H \"$it\"")
    }
    // Add body
    body?.let {
        append("\\\n")
        append("  -d '$it'")
    }
}