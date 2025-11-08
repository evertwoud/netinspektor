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
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.random.Random

@Composable
fun ClientComponent(
    modifier: Modifier = Modifier,
    client: HttpClient,
    scope: CoroutineScope,
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

    fun makeRequest(url: String) {
        scope.launch(Dispatchers.IO) {
            request = NetInspektorEvent.Request(
                method = "GET",
                url = url,
                headers = emptyMap(),
                body = null
            )
            session.logRequest(request!!)
            requestCount++
            val response = client.request(url)
            session.logResponse(
                NetInspektorEvent.Response(
                    requestUuid = request!!.uuid,
                    headers = response.headers.toMap().map { (key, values) -> key to values.joinToString("; ") }
                        .toMap(),
                    statusCode = response.status.value,
                    statusDescription = response.status.description,
                    body = response.bodyAsText()
                )
            )
        }
    }

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
                        content = { Text("Products", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        onClick = { makeRequest("https://dummyjson.com/products") },
                    )

                    OutlinedButton(
                        modifier = Modifier.weight(1F),
                        content = { Text("Carts", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        onClick = { makeRequest("https://dummyjson.com/carts") },
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1F),
                        content = { Text("Comments", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        onClick = { makeRequest("https://dummyjson.com/comments") },
                    )

                    OutlinedButton(
                        modifier = Modifier.weight(1F),
                        content = { Text("Error", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        onClick = { makeRequest("https://dummyjson.com/error") },
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