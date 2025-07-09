package com.evertwoud.netinspektor.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evertwoud.netinspektor.client.session.NetInspektorSession
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.random.Random

@Composable
fun ClientComponent(
    modifier: Modifier = Modifier,
    session: NetInspektorSession,
    index: Int,
    onRemove: () -> Unit,
) {
    var request by remember { mutableStateOf<NetInspektorEvent.Request?>(null) }
    var requestCount by remember { mutableIntStateOf(0) }
    var responseCount by remember { mutableIntStateOf(0) }

    val isSessionRunning by session.server.running.collectAsStateWithLifecycle()
    val clients by session.server.clientCount.collectAsStateWithLifecycle()
    val port by session.server.port.collectAsStateWithLifecycle()

    LaunchedEffect(session) {
        if (!isSessionRunning) session.start()
    }
    SelectionContainer {
        Card {
            Column(
                modifier = modifier.fillMaxSize().padding(12.dp),
            ) {
                Row(modifier = modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("Server $index")
                        Text(
                            text = """
                            Name: ${session.sessionName}
                            ----------
                            Port: $port
                            Clients: $clients
                            ----------
                            Request count: $requestCount
                            Response count: $responseCount
                            """.trimIndent(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Button(
                        content = { Text(if (isSessionRunning) "Stop" else "Start") },
                        onClick = {
                            if (isSessionRunning) {
                                session.stop()
                            } else {
                                session.start()
                            }
                        },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1F),
                        content = { Text("Request", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        onClick = {
                            request = NetInspektorEvent.Request(
                                method = "GET",
                                url = "https://www.evertwoud.com/",
                                headers = emptyMap(),
                                body = null
                            )
                            session.logRequest(request!!)
                            requestCount++
                        },
                    )

                    OutlinedButton(
                        modifier = Modifier.weight(1F),
                        content = { Text("Response", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        enabled = request != null,
                        onClick = {
                            val success = Random.nextBoolean()
                            session.logResponse(
                                NetInspektorEvent.Response(
                                    requestUuid = request!!.uuid,
                                    headers = mapOf(
                                        "Host" to "code.tutsplus.com",
                                        "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                                        "Accept-Language" to "en-us,en;q=0.5",
                                        "Accept-Encoding" to "gzip,deflate",
                                    ),
                                    statusCode = when (success) {
                                        true -> 200
                                        false -> 500
                                    },
                                    body = when (success) {
                                        true -> buildJsonObject {
                                            put("response", "ok!")
                                            put("works", true)
                                            put("timestamp", request!!.timestamp)
                                        }

                                        false -> buildJsonObject {
                                            put("response", "false!")
                                            put("works", false)
                                        }
                                    }.toString()
                                )
                            )
                            responseCount++
                        },
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(
                        modifier = Modifier.weight(1F),
                        content = { Text("Clear history") },
                        onClick = {
                            session.clearHistory()
                            requestCount = 0
                            responseCount = 0
                        },
                    )

                    TextButton(
                        modifier = Modifier.weight(1F),
                        content = { Text("Delete") },
                        onClick = onRemove,
                    )
                }
            }
        }
    }
}