package com.evertwoud.netinspektor.client.session

import com.evertwoud.netinspektor.client.discovery.DiscoveryClient
import com.evertwoud.netinspektor.client.server.SessionServer
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.core.socket.NetInspektorSessionHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class NetInspektorSession @OptIn(ExperimentalUuidApi::class) constructor(
    val uuid: String = Uuid.random().toString(),
    val sessionName: String
) {

    private val requests = mutableListOf<NetInspektorEvent.Request>()
    private val responses = mutableListOf<NetInspektorEvent.Response>()

    val history
        get() = NetInspektorSessionHistory(
            requests = requests,
            responses = responses,
        )

    val server = SessionServer(session = this)

    fun start() = server.start(
        onServiceAvailable = { host, port -> announce(host, port) }
    )

    fun stop() {
        clearHistory()
        server.stop()
    }

    fun logRequest(request: NetInspektorEvent.Request) {
        requests.add(request)
        server.emitEvent(request)
    }

    fun logResponse(response: NetInspektorEvent.Response) {
        responses.add(response)
        server.emitEvent(response)
    }

    fun clearHistory() {
        requests.clear()
        responses.clear()
    }

    private suspend fun announce(host: String, port: Int) {
        withContext(Dispatchers.IO) {
            try {
                DiscoveryClient(
                    sessionName = sessionName,
                    host = host,
                    port = port,
                ).connect()
            } catch (e: Exception) {
                // Recursively retry announcement
                delay(5.seconds)
                announce(host, port)
            }
        }
    }
}