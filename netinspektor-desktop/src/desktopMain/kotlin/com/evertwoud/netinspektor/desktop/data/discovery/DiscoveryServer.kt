package com.evertwoud.netinspektor.desktop.data.discovery

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.evertwoud.netinspektor.core.NetInspektorConstants.DISCOVERY_SERVER_PATH
import com.evertwoud.netinspektor.core.NetInspektorConstants.DISCOVERY_SERVER_PORT
import com.evertwoud.netinspektor.core.socket.NetInspektorDevice
import com.evertwoud.netinspektor.desktop.data.discovery.adb.AdbService
import com.evertwoud.netinspektor.desktop.ext.decodeToJsonElement
import com.evertwoud.netinspektor.desktop.util.SerializerUtil
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DiscoveryServer(
    val adb: AdbService
) {
    var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    private val json = SerializerUtil.json

    val discoveredDevices = mutableStateListOf<NetInspektorDevice>()

    val isRunning by derivedStateOf {
        server?.application != null
    }

    suspend fun start() {
        server = create()
        server?.startSuspend(wait = false)
    }

    suspend fun stop() {
        server?.stopSuspend()
        server = null
    }

    private fun create() = embeddedServer(
        factory = CIO,
        host = "0.0.0.0",
        port = DISCOVERY_SERVER_PORT
    ) {
        install(WebSockets.Plugin)
        routing {
            webSocket(DISCOVERY_SERVER_PATH) {
                // Process incoming events
                incoming.consumeAsFlow().collect { frame ->
                    try {
                        if (frame is Frame.Text) {
                            val element = frame.decodeToJsonElement(json)
                            val data = element.jsonObject["data"]?.jsonObject
                            println("Frame decoded: $element, data: $data")
                            if (data != null) {
                                // Process type
                                when (element.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
                                    // Send the session history
                                    "announce" -> json.decodeFromJsonElement(
                                        deserializer = NetInspektorDevice.serializer(),
                                        element = data
                                    ).let { device ->
                                        println("Device availability announced: $device")
                                        // Attempt device bridging
                                        println("Device forwarding started")
                                        val didForward = adb.discoverAndForward(port = device.port)
                                        val newDevice = when (didForward) {
                                            // If port was forwarded, migrate to local host
                                            true -> device.copy(host = "127.0.0.1")
                                            false -> device
                                        }
                                        // Add the device
                                        discoveredDevices.add(newDevice)
                                        // Process disconnection
                                        launch {
                                            closeReason.await()?.let {
                                                println("Client disconnected: $it")
                                                discoveredDevices.remove(newDevice)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        println("Unable to process frame $frame, $e")
                    }
                }
            }
        }
    }
}