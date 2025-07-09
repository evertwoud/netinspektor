package com.evertwoud.netinspektor.desktop.data.session

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.evertwoud.netinspektor.core.NetInspektorConstants.EVENT_SERVER_PATH
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.core.socket.NetInspektorInitializeMessage
import com.evertwoud.netinspektor.core.socket.NetInspektorMetadata
import com.evertwoud.netinspektor.core.socket.NetInspektorSessionHistory
import com.evertwoud.netinspektor.desktop.data.model.SessionData
import com.evertwoud.netinspektor.desktop.ext.decodeToJsonElement
import com.evertwoud.netinspektor.desktop.ext.getOrMatchRequest
import com.evertwoud.netinspektor.desktop.ext.send
import com.evertwoud.netinspektor.desktop.util.SerializerUtil
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SessionClient @OptIn(ExperimentalUuidApi::class) constructor(
    val uuid: String = Uuid.random().toString(),
    val host: String,
    val port: Int,
) {
    private val json = SerializerUtil.json

    val searchQuery = TextFieldState(initialText = "")

    val data = SessionData()

    var metadata by mutableStateOf<NetInspektorMetadata?>(null)
        private set

    var running by mutableStateOf(false)

    val filteredEvents by derivedStateOf {
        data.events.toList().filter { event ->
            // Filter matching urls
            when (searchQuery.text.isNotEmpty()) {
                true -> event.getOrMatchRequest(this)?.url?.contains(
                    other = searchQuery.text,
                    ignoreCase = true
                ) ?: false

                false -> true
            }
        }.distinctBy { it.uuid }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun connect(
        onInitialized: () -> Unit,
        onClose: () -> Unit
    ) {
        val client = HttpClient(CIO) {
            install(WebSockets)
        }
        client.webSocket(
            method = HttpMethod.Get,
            host = host,
            port = port,
            path = EVENT_SERVER_PATH
        ) {
            // Process disconnection
            launch {
                closeReason.await()?.let {
                    println("Client disconnected: $it")
                    onClose()
                }
            }
            // Send the initialization message
            send(
                json = json,
                type = "init",
                data = NetInspektorInitializeMessage(uuid = uuid)
            )
            // Process incoming events
            incoming.consumeAsFlow().collect { frame ->
                try {
                    when (frame) {
                        is Frame.Text -> {
                            val element = frame.decodeToJsonElement(json)
                            val data = element.jsonObject["data"]?.jsonObject
                            println("Frame decoded: $element, data: $data")
                            if (data != null) {
                                // Process type
                                when (element.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
                                    "metadata" -> json.decodeFromJsonElement(
                                        deserializer = NetInspektorMetadata.serializer(),
                                        element = data
                                    ).let {
                                        processMetadata(it)
                                        onInitialized()
                                    }
                                    // Send the session history
                                    "history" -> json.decodeFromJsonElement(
                                        deserializer = NetInspektorSessionHistory.serializer(),
                                        element = data
                                    ).let { processHistory(it) }

                                    "log-request" -> json.decodeFromJsonElement(
                                        deserializer = NetInspektorEvent.Request.serializer(),
                                        element = data
                                    ).let { processRequest(it) }

                                    "log-response" -> json.decodeFromJsonElement(
                                        deserializer = NetInspektorEvent.Response.serializer(),
                                        element = data
                                    ).let { processResponse(it) }
                                }
                            }
                        }

                        is Frame.Close -> onClose()
                        else -> println("Unprocessed frame: $frame")
                    }
                } catch (e: Throwable) {
                    println("Unable to process frame $frame")
                }
            }
        }
    }

    fun processMetadata(metadata: NetInspektorMetadata) {
        this.metadata = metadata
    }

    fun processHistory(history: NetInspektorSessionHistory) {
        data.requests.addAll(history.requests)
        data.responses.addAll(history.responses)
    }

    fun processRequest(request: NetInspektorEvent.Request) {
        data.requests.add(request)
    }

    fun processResponse(response: NetInspektorEvent.Response) {
        data.responses.add(response)
    }
}
