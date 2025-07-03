package com.evertwoud.netinspektor.desktop.ext

import androidx.compose.runtime.Composable
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.data.session.SessionClient
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.theme.colorPalette

@Composable
fun NetInspektorEvent.Response.statusCodeColor(index : Int) = when (statusCode) {
    in 0..100 -> JewelTheme.colorPalette.purple
    in 100..300 -> JewelTheme.colorPalette.green
    in 300..400 -> JewelTheme.colorPalette.yellow
    in 400..600 -> JewelTheme.colorPalette.red
    else -> JewelTheme.colorPalette.gray
}[index]

fun NetInspektorEvent.getOrMatchRequest(session: SessionClient?): NetInspektorEvent.Request? = when (this) {
    is NetInspektorEvent.Request -> this
    is NetInspektorEvent.Response -> session?.data?.matchRequestFor(this)
}

fun NetInspektorEvent.getOrMatchResponse(session: SessionClient?): NetInspektorEvent.Response? = when (this) {
    is NetInspektorEvent.Request -> session?.data?.matchResponsesFor(this)?.lastOrNull()
    is NetInspektorEvent.Response -> this
}