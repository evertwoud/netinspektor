package com.evertwoud.netinspektor.desktop

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.data.FormatStyle
import com.evertwoud.netinspektor.desktop.data.discovery.DiscoveryService
import com.evertwoud.netinspektor.desktop.data.session.SessionClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

/**
 * ViewModel for the main screen of the application.
 */
@OptIn(ExperimentalUuidApi::class)
class MainViewModel : ViewModel() {
    private var discoveryJob: Job? = null
    val discovery = DiscoveryService()

    // State properties
    val sessions = mutableStateListOf<SessionClient>()
    var alwaysOnTop by mutableStateOf(true)
    var session by mutableStateOf<SessionClient?>(null)
    var selection by mutableStateOf<NetInspektorEvent?>(null)
    var formatStyle by mutableStateOf(FormatStyle.Pretty)
    val linkedEvents = derivedStateOf {
        session?.data?.matchLinkedEvents(selection)
    }

    init {
        initDiscovery()
    }

    /**
     * Initialize the discovery service
     */
    fun initDiscovery() {
        discoveryJob?.cancel()
        discoveryJob = viewModelScope.launch {
            discovery.init()
        }
    }

    /**
     * Connect to a session at the specified address and port.
     */
    fun connect(
        address: String,
        port: String,
        onConnected: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            SessionClient(
                host = address,
                port = port.toIntOrNull() ?: 0
            ).let { client ->
                client.connect(
                    onInitialized = {
                        println("Socket connected: ${client.uuid}")
                        client.running = true
                        sessions.add(client)
                        session = client
                        selection = null
                        onConnected()
                    },
                    onClose = {
                        client.running = false
                    }
                )
            }
        }
    }

    /**
     * Disconnect from the specified session.
     */
    fun disconnect(session: SessionClient) {
        session.running = false
        sessions.remove(session)

        if (this.session == session) {
            this.selection = null
            this.session = sessions.firstOrNull()
        }
    }
}
