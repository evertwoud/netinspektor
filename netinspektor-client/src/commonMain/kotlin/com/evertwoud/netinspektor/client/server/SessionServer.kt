package com.evertwoud.netinspektor.client.server

import com.evertwoud.netinspektor.client.ext.decodeToJsonElement
import com.evertwoud.netinspektor.client.ext.send
import com.evertwoud.netinspektor.client.platformIdentifier
import com.evertwoud.netinspektor.client.session.NetInspektorSession
import com.evertwoud.netinspektor.client.util.SerializerUtil
import com.evertwoud.netinspektor.core.NetInspektorConstants.AUTO_ASSIGNED_PORT
import com.evertwoud.netinspektor.core.NetInspektorConstants.EVENT_SERVER_PATH
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.core.socket.NetInspektorMetadata
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds

class SessionServer(val session: NetInspektorSession) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val json = SerializerUtil.json

    var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    val events = MutableSharedFlow<NetInspektorEvent>()
    val port = MutableStateFlow("not-set")
    val running = MutableStateFlow(false)
    val clientCount = MutableStateFlow(0)

    var serverJob: Job? = null

    fun start(
        onServiceAvailable: suspend (host: String, port: Int) -> Unit,
    ) {
        serverJob = scope.launch(Dispatchers.IO) {
            server = create()
            server?.startSuspend(wait = false)
            server?.application?.engine?.resolvedConnectors()?.firstOrNull()?.let {
                port.emit(it.port.toString())
                clientCount.emit(0)
                running.emit(true)
                onServiceAvailable(it.host, it.port)
            }
        }
        serverJob?.invokeOnCompletion {
            scope.launch {
                server?.stopSuspend()
                server = null
                clientCount.emit(0)
                running.emit(false)
                port.emit("not-set")
            }
        }
    }

    fun stop() = serverJob?.cancel()

    fun emitEvent(event: NetInspektorEvent) = scope.launch {
        events.emit(event)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun create() = embeddedServer(
        factory = CIO,
        host = "localhost",
        port = AUTO_ASSIGNED_PORT
    ) {
        install(WebSockets.Plugin) {
            this.pingPeriod = 3.seconds
        }
        routing {
            webSocket(EVENT_SERVER_PATH) {
                // Process outgoing events
                launch {
                    events.collect { event ->
                        when (event) {
                            is NetInspektorEvent.Request -> send(
                                json = json,
                                type = "log-request",
                                data = event
                            )

                            is NetInspektorEvent.Response -> send(
                                json = json,
                                type = "log-response",
                                data = event
                            )
                        }
                    }
                }
                // Process incoming events
                incoming.consumeAsFlow().collect { frame ->
                    try {
                        if (frame is Frame.Text) {
                            val element = frame.decodeToJsonElement(json)
                            println("Frame decoded: $element")
                            // Process type
                            when (element.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
                                // Send the session history
                                "init" -> {
                                    // Increase client count
                                    clientCount.emit(clientCount.value + 1)
                                    // Provide session metadata
                                    send(
                                        json = json,
                                        type = "metadata",
                                        data = NetInspektorMetadata(
                                            uuid = session.uuid,
                                            name = session.sessionName,
                                            platform = platformIdentifier
                                        )
                                    )
                                    // Provide history
                                    send(
                                        json = json,
                                        type = "history",
                                        data = session.history
                                    )
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        println("Unable to process frame $frame")
                    }
                }
            }
        }
    }
}