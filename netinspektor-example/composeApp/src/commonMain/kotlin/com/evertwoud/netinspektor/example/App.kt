package com.evertwoud.netinspektor.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evertwoud.netinspektor.client.platformIdentifier
import com.evertwoud.netinspektor.client.session.NetInspektorSession
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() {
    val sessions = remember { mutableStateListOf<NetInspektorSession>() }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold { padding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalButton(
                    content = { Text("Add session") },
                    onClick = {
                        sessions.add(
                            NetInspektorSession(
                                sessionName = "example-$platformIdentifier (${sessions.size + 1})"
                            )
                        )
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))

                sessions.forEachIndexed { index, session ->
                    ClientComponent(
                        modifier = Modifier.fillMaxWidth(),
                        session = session,
                        index = index
                    ) {
                        session.stop()
                        sessions.removeAt(index)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}