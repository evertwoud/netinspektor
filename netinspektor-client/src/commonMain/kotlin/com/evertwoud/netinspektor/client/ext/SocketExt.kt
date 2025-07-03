package com.evertwoud.netinspektor.client.ext

import com.evertwoud.netinspektor.core.socket.NetInspektorSocketMessage
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readBytes
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
suspend inline fun <reified T> WebSocketSession.send(
    json: Json,
    type: String,
    data: T
) {
    val content = json.encodeToString(NetInspektorSocketMessage(type = type, data = data))
    val encoded = Base64.Default.encode(content.encodeToByteArray())
    send(Frame.Text(encoded))
    println("Frame sent: $content, data: $data, encoded: $encoded")
}

@OptIn(ExperimentalEncodingApi::class)
fun Frame.Text.decodeToJsonElement(json: Json) : JsonElement {
    val raw = readBytes()
    val decoded = Base64.Default.decode(raw).decodeToString()
    return json.decodeFromString<JsonElement>(decoded)
}