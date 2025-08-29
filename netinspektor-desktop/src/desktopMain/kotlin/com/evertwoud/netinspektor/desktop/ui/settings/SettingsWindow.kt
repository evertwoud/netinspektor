package com.evertwoud.netinspektor.desktop.ui.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.evertwoud.netinspektor.desktop.MainViewModel
import com.evertwoud.netinspektor.desktop.util.AppControls
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.dark
import org.jetbrains.jewel.intui.standalone.styling.default
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Typography
import org.jetbrains.jewel.ui.component.VerticallyScrollableContainer
import org.jetbrains.jewel.ui.component.scrollbarContentSafePadding
import org.jetbrains.jewel.ui.component.styling.ScrollbarStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarVisibility
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsWindow(
    controls: AppControls,
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scrollbarStyle = remember {
        val base = ScrollbarStyle.dark()
        ScrollbarStyle(
            colors = base.colors,
            metrics = base.metrics,
            trackClickBehavior = base.trackClickBehavior,
            scrollbarVisibility = ScrollbarVisibility.WhenScrolling.default(),
        )
    }

    DecoratedWindow(
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 292.dp,
            height = 256.dp
        ),
        onCloseRequest = onClose,
        title = "Pairing",
        alwaysOnTop = viewModel.alwaysOnTop,
        resizable = true,
        content = {
            TitleBar(
                modifier = Modifier.newFullscreenControls(),
                gradientStartColor = JewelTheme.colorPalette.purple(1),
            ) {
                Text("Settings")
            }
            VerticallyScrollableContainer(
                scrollState = scrollState,
                style = scrollbarStyle,
                modifier = Modifier.fillMaxSize().background(JewelTheme.colorPalette.gray(1))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(JewelTheme.colorPalette.gray(1))
                        .padding(8.dp)
                        .padding(end = scrollbarContentSafePadding())
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = viewModel.alwaysOnTop,
                            onCheckedChange = {
                                viewModel.alwaysOnTop = it
                            }
                        )
                        Text(
                            modifier = Modifier.weight(1F),
                            text = "Window always on top",
                        )
                    }
                }
            }
        }
    )
}

