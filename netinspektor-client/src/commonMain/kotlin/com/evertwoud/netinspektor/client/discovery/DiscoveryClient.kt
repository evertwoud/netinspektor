package com.evertwoud.netinspektor.client.discovery

import com.evertwoud.netinspektor.client.ext.decodeToJsonElement
import com.evertwoud.netinspektor.client.ext.send
import com.evertwoud.netinspektor.client.platformIdentifier
import com.evertwoud.netinspektor.client.util.SerializerUtil
import com.evertwoud.netinspektor.core.NetInspektorConstants
import com.evertwoud.netinspektor.core.socket.NetInspektorDevice
import dev.tmapps.konnection.Konnection
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class DiscoveryClient(
    val sessionName: String,
    val host: String,
    val port: Int,
) {
    private val client = HttpClient(CIO) {
        install(WebSockets.Plugin)
    }
    private val json = SerializerUtil.json

    suspend fun connect() = client.webSocket(
        method = HttpMethod.Companion.Get,
        host = "localhost",
        port = NetInspektorConstants.DISCOVERY_SERVER_PORT,
        path = NetInspektorConstants.DISCOVERY_SERVER_PATH
    ) {
        // Send the initialization message
        send(
            json = json,
            type = "announce",
            data = NetInspektorDevice(
                sessionName = sessionName,
                platform = platformIdentifier,
                host = host,
                port = port,
            )
        )
        // Process incoming events
        incoming.consumeAsFlow().collect { frame ->
            try {
                if (frame is Frame.Text) {
                    val element = frame.decodeToJsonElement(json)
                    println("Frame decoded: $element")
                    // Process type
                    when (element.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
                        else -> println("Unprocessed frame: $frame")
                    }
                }
            } catch (e: Throwable) {
                println("Unable to process frame $frame")
            }
        }
    }
}