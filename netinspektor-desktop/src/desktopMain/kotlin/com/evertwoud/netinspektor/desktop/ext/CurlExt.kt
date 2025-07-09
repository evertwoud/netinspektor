package com.evertwoud.netinspektor.desktop.ext

import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.util.FormatConstants

fun NetInspektorEvent.Request.toCurlRequest() = buildString {
    // Start with method
    append("curl -X ${method.uppercase()}")
    // Append url
    append("\\\n")
    append(FormatConstants.INDENT)
    append("'$url'")
    // Add headers
    headers.forEach { (key, value) ->
        append("\\\n")
        append(FormatConstants.INDENT)
        append("-H \"$key: $value\"")
    }
    // Add body
    body?.let {
        append("\\\n")
        append(FormatConstants.INDENT)
        append("-d '$it'")
    }
}