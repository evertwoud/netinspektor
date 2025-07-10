package com.evertwoud.netinspektor.desktop.data.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.TextRange
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.ext.getOrMatchRequest

class SessionData {
    var archive by mutableStateOf(Archive())
        private set

    val requests = mutableStateListOf<NetInspektorEvent.Request>()
    val responses = mutableStateListOf<NetInspektorEvent.Response>()

    val events by derivedStateOf(neverEqualPolicy()) {
        (requests.toList() + responses.toList()).sortedBy { it.timestamp }
    }

    fun restoreArchive() {
        requests.addAll(archive.requests)
        responses.addAll(archive.responses)
        archive = Archive()
    }

    fun archive() {
        // Write data to archive
        archive.write(requests.toList(), responses.toList())
        // Clear current data
        requests.clear()
        responses.clear()
    }

    fun matchRequestFor(
        response: NetInspektorEvent.Response
    ): NetInspektorEvent.Request? = requests.toList().firstOrNull { request ->
        response.requestUuid == request.uuid
    }

    fun matchResponsesFor(
        request: NetInspektorEvent.Request
    ): List<NetInspektorEvent.Response> = responses.toList().filter { response ->
        response.requestUuid == request.uuid
    }

    fun matchLinkedEvents(selection: NetInspektorEvent?) = when (selection) {
        is NetInspektorEvent.Request -> matchResponsesFor(selection)
        is NetInspektorEvent.Response -> listOfNotNull(matchRequestFor(selection))
        else -> emptyList()
    }


    class Archive() {
        val requests = mutableStateListOf<NetInspektorEvent.Request>()
        val responses = mutableStateListOf<NetInspektorEvent.Response>()

        val isEmpty by derivedStateOf { requests.isEmpty() && responses.isEmpty() }

        fun write(
            requests: List<NetInspektorEvent.Request>,
            responses: List<NetInspektorEvent.Response>
        ) {
            this.requests.addAll(requests)
            this.responses.addAll(responses)
        }
    }
}