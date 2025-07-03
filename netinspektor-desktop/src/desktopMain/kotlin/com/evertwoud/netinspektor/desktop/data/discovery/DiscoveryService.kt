package com.evertwoud.netinspektor.desktop.data.discovery

import com.evertwoud.netinspektor.desktop.data.discovery.adb.AdbService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoveryService(
    val adb: AdbService = AdbService(),
    val server: DiscoveryServer = DiscoveryServer(adb)
) {
    val devices get() = server.discoveredDevices

    suspend fun init() = withContext(Dispatchers.IO) {
        // Discover devices
        launch { adb.init() }
        // Launch server
        launch { server.start() }
    }
}