package com.evertwoud.netinspektor.desktop.ui.pairing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.evertwoud.netinspektor.desktop.MainViewModel
import org.jetbrains.jewel.foundation.lazy.SelectableLazyColumn
import org.jetbrains.jewel.foundation.lazy.SelectionMode
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.intui.standalone.theme.createEditorTextStyle
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.component.Typography
import org.jetbrains.jewel.ui.component.styling.LocalLazyTreeStyle
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PairingWindow(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    var connectionFailed by remember { mutableStateOf(false) }

    val address = rememberTextFieldState("127.0.0.1")
    val port = rememberTextFieldState()

    DecoratedWindow(
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 292.dp,
            height = 256.dp
        ),
        onCloseRequest = onClose,
        title = "Pairing",
        alwaysOnTop = false,
        resizable = true,
        content = {
            TitleBar(
                modifier = Modifier.newFullscreenControls(),
                gradientStartColor = JewelTheme.colorPalette.purple(1),
            ) {
                Text("Pairing")
            }
            Column(modifier = Modifier.fillMaxSize().background(JewelTheme.colorPalette.gray(1))) {
                if (!viewModel.discovery.server.isRunning) {
                    Text(
                        modifier = Modifier.fillMaxWidth().background(JewelTheme.colorPalette.red(7)).padding(8.dp),
                        text = "⚠ Discovery server not running",
                        color = JewelTheme.colorPalette.gray(1)
                    )
                }
                SelectableLazyColumn(
                    modifier = Modifier.weight(1F).fillMaxWidth(),
                    selectionMode = SelectionMode.Single,
                    onSelectedIndexesChange = { indexes ->
                        if (viewModel.discovery.devices.isNotEmpty()) {
                            val matchingDevice = indexes.firstOrNull()?.let {
                                viewModel.discovery.devices.getOrNull(it)
                            }
                            if (matchingDevice != null) {
                                address.edit { replace(0, length, matchingDevice.host) }
                                port.edit { replace(0, length, matchingDevice.port.toString()) }
                            }
                        }
                    }
                ) {
                    if (viewModel.discovery.devices.isNotEmpty()) {
                        items(
                            count = viewModel.discovery.devices.size,
                            key = { index ->
                                viewModel.discovery.devices.getOrNull(index)?.let {
                                    "${it.sessionName}[${it.host}:${it.port}]"
                                } ?: "not-set"
                            }
                        ) { index ->
                            val device = viewModel.discovery.devices.getOrNull(index) ?: return@items
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .background(
                                        when {
                                            isSelected && isActive -> LocalLazyTreeStyle.current.colors.backgroundSelectedFocused
                                            isSelected && !isActive -> LocalLazyTreeStyle.current.colors.backgroundSelected
                                            else -> Color.Transparent
                                        },
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = device.sessionName,
                                    style = Typography.labelTextStyle()
                                )
                                Text(
                                    text = "${device.platform} | ${device.host}:${device.port}",
                                    color = JewelTheme.colorPalette.gray(9)
                                )
                            }
                        }
                    } else {
                        item("empty") {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                text = "No results found", style = Typography.labelTextStyle()
                            )
                        }
                    }
                }
                HorizontalDivider(color = JewelTheme.globalColors.borders.disabled)
                Row(
                    modifier = Modifier.fillMaxWidth().background(JewelTheme.globalColors.panelBackground)
                        .padding(12.dp)
                ) {
                    TextField(
                        modifier = Modifier.weight(1F),
                        state = address,
                        outline = when (connectionFailed) {
                            true -> Outline.Error
                            false -> Outline.None
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        modifier = Modifier.width(64.dp),
                        state = port,
                    )
                    Spacer(Modifier.width(8.dp))
                    DefaultButton(
                        content = { Text("Pair") },
                        enabled = address.text.isNotEmpty() && port.text.isNotEmpty(),
                        onClick = {
                            viewModel.connect(
                                address = address.text.toString(),
                                port = port.text.toString(),
                                onConnected = onClose,
                            )
                        },
                    )
                }
            }
        }
    )
}