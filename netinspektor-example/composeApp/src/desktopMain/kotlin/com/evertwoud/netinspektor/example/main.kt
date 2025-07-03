package com.evertwoud.netinspektor.example

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        state = rememberWindowState(
            size = DpSize(256.dp, 512.dp),
            position = WindowPosition.Aligned(Alignment.CenterStart),
        ),
        onCloseRequest = ::exitApplication,
        title = "Netinspektor Example",
    ) {
        App()
    }
}