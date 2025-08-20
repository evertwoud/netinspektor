@file:Suppress("UnstableApiUsage")

package com.evertwoud.netinspektor.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.evertwoud.netinspektor.desktop.ui.AppDestination
import com.evertwoud.netinspektor.desktop.ui.events.EventOverviewScreen
import com.evertwoud.netinspektor.desktop.ui.pairing.PairingWindow
import com.evertwoud.netinspektor.desktop.ui.settings.SettingsWindow
import com.evertwoud.netinspektor.desktop.util.AppControls
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.Editor
import org.jetbrains.jewel.intui.standalone.theme.*
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.component.styling.TabStyle
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.painter.hints.Size
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarStyle

@OptIn(ExperimentalLayoutApi::class)
fun main() = application {
    val viewModelStoreOwner = object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore
            get() = ViewModelStore()
    }
    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        val viewModel = viewModel { MainViewModel() }
        val navController = rememberNavController()
        val controls = AppControls(navController = navController)

        var showPairingWindow by remember { mutableStateOf(false) }
        var showSettingsWindow by remember { mutableStateOf(false) }

        val tabs by remember {
            derivedStateOf {
                viewModel.sessions.map { session ->
                    TabData.Default(
                        selected = viewModel.session == session,
                        content = { tabState ->
                            SimpleTabContent(
                                label = session.metadata?.name ?: session.uuid,
                                state = tabState,
                                icon = when (session.running) {
                                    true -> null
                                    false -> painterResource(AllIconsKeys.Actions.OfflineMode.newUiPath)
                                }
                            )
                        },
                        onClose = {
                            viewModel.disconnect(session)
                        },
                        onClick = {
                            viewModel.selection = null
                            viewModel.session = session
                        },
                    )
                }
            }
        }

        IntUiTheme(
            theme = JewelTheme.darkThemeDefinition(
                defaultTextStyle = JewelTheme.createDefaultTextStyle(),
                editorTextStyle = JewelTheme.createEditorTextStyle()
            ),
            styling = ComponentStyling.default().decoratedWindow(
                titleBarStyle = TitleBarStyle.dark()
            ),
            swingCompatMode = false,
        ) {
            DecoratedWindow(
                state = rememberWindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    width = 1024.dp,
                    height = 768.dp
                ),
                onCloseRequest = { exitApplication() },
                title = "netinspektor",
                alwaysOnTop = viewModel.alwaysOnTop,
                content = {
                    TitleBar(
                        modifier = Modifier.newFullscreenControls(),
                        gradientStartColor = when (viewModel.session) {
                            null -> JewelTheme.colorPalette.red.first()
                            else -> JewelTheme.colorPalette.green.first()
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(
                                start = 32.dp,
                                end = 44.dp,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TabStrip(
                                modifier = Modifier.weight(1f),
                                tabs = tabs,
                                style = TabStyle.Editor.dark()
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                content = {
                                    Icon(
                                        AllIconsKeys.General.Add,
                                        null,
                                        modifier = Modifier.size(32.dp).padding(8.dp),
                                        hint = Size(32)
                                    )
                                },
                                onClick = { showPairingWindow = true }
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                content = {
                                    Icon(
                                        AllIconsKeys.General.Settings,
                                        null,
                                        modifier = Modifier.size(32.dp).padding(8.dp),
                                        hint = Size(32)
                                    )
                                },
                                onClick = { showSettingsWindow = true }
                            )
                        }
                    }
                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = AppDestination.Events
                    ) {
                        composable<AppDestination.Events> {
                            EventOverviewScreen(
                                controls = controls,
                                viewModel = viewModel,
                            )
                        }
                    }
                }
            )

            if (showPairingWindow) PairingWindow(
                viewModel = viewModel,
                onClose = {
                    showPairingWindow = false
                }
            )

            if (showSettingsWindow) SettingsWindow(
                controls = controls,
                viewModel = viewModel,
                onClose = {
                    showSettingsWindow = false
                }
            )
        }
    }
}