package com.evertwoud.netinspektor.desktop.data.discovery.adb

import com.evertwoud.netinspektor.core.NetInspektorConstants.DISCOVERY_SERVER_PORT
import com.malinskiy.adam.AndroidDebugBridgeClient
import com.malinskiy.adam.AndroidDebugBridgeClientFactory
import com.malinskiy.adam.interactor.StartAdbInteractor
import com.malinskiy.adam.request.device.AsyncDeviceMonitorRequest
import com.malinskiy.adam.request.device.ListDevicesRequest
import com.malinskiy.adam.request.forwarding.LocalTcpPortSpec
import com.malinskiy.adam.request.forwarding.PortForwardRequest
import com.malinskiy.adam.request.forwarding.PortForwardingMode
import com.malinskiy.adam.request.forwarding.RemoteTcpPortSpec
import com.malinskiy.adam.request.forwarding.RemoveAllPortForwardsRequest
import com.malinskiy.adam.request.reverse.RemoveAllReversePortForwardsRequest
import com.malinskiy.adam.request.reverse.ReversePortForwardRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.withContext

class AdbService {

    var adb: AndroidDebugBridgeClient = AndroidDebugBridgeClientFactory().build()

    suspend fun init() {
        // Start adb server
        StartAdbInteractor().execute()
        // Start with clean state
        reset()
        // Start discovery
        discoverAndReverse(port = DISCOVERY_SERVER_PORT)
    }

    suspend fun reset() = withContext(Dispatchers.IO) {
        devices().let { devices ->
            println("(adb) Clearing all forwards on devices: ${devices.joinToString(",") { it.serial }}")
            devices.forEach { device ->
                clearDevice(device.serial)
            }
        }
    }

    suspend fun discoverAndReverse(port: Int) = withContext(Dispatchers.IO) {
        discover().collect { devices ->
            println("(adb) Device reversing started; port: $port; devices: ${devices.joinToString(",") { it.serial }}")
            devices.forEach { device ->
                reverse(
                    serial = device.serial,
                    port = port,
                )
            }
        }
    }

    suspend fun discoverAndForward(port: Int): Boolean = withContext(Dispatchers.IO) {
        var didForward = false
        devices().let { devices ->
            println("(adb) Device forwarding started; port: $port; devices: ${devices.joinToString(",") { it.serial }}")
            devices.forEach { device ->
                forward(
                    serial = device.serial,
                    port = port,
                )
                didForward = true
            }
        }
        return@withContext didForward
    }

    private fun CoroutineScope.discover() = adb.execute(AsyncDeviceMonitorRequest(), scope = this).consumeAsFlow()

    private suspend fun devices() = adb.execute(request = ListDevicesRequest())

    private suspend fun reverse(serial: String, port: Int) = try {
        adb.execute(
            request = ReversePortForwardRequest(
                local = RemoteTcpPortSpec(port),
                remote = LocalTcpPortSpec(port),
                mode = PortForwardingMode.NO_REBIND
            ),
            serial = serial
        )
        println("(adb) Device port reversed; port:$port")
    } catch (e: Exception) {
        println("(adb Device port could not be reversed: ${e.message}")
    }

    suspend fun forward(serial: String, port: Int) = try {
        adb.execute(
            request = PortForwardRequest(
                local = LocalTcpPortSpec(port),
                remote = RemoteTcpPortSpec(port),
                mode = PortForwardingMode.DEFAULT,
                serial = serial
            ),
            serial = serial
        )
        println("(adb) Device port forwarded; port:$port")
    } catch (e: Exception) {
        println("(adb Device port could not be forwarded: ${e.message}")
    }

    suspend fun clearDevice(serial: String) = try {
        adb.execute(
            request = RemoveAllPortForwardsRequest(serial = serial),
            serial = serial
        )
        adb.execute(
            request = RemoveAllReversePortForwardsRequest(),
            serial = serial
        )
        println("(adb) Device ports cleared")
    } catch (e: Exception) {
        println("(adb Device ports could not be cleared: ${e.message}")
    }
}