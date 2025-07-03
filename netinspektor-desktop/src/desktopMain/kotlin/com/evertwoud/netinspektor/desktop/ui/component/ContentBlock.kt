@file:Suppress("UnstableApiUsage")

package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.Stroke
import org.jetbrains.jewel.foundation.modifier.border
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.dark
import org.jetbrains.jewel.intui.standalone.styling.default
import org.jetbrains.jewel.intui.standalone.styling.light
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.component.styling.*
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.theme.iconButtonStyle
import org.jetbrains.jewel.ui.theme.textAreaStyle
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class, ExperimentalJewelApi::class)
@Composable
fun ContentBlock(
    modifier: Modifier = Modifier,
    content: String?,
    allowCopy: Boolean = true,
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val scrollbarStyle = remember {
        val base = ScrollbarStyle.light()
        ScrollbarStyle(
            colors = base.colors,
            metrics = base.metrics,
            trackClickBehavior = base.trackClickBehavior,
            scrollbarVisibility = ScrollbarVisibility.WhenScrolling.default(),
        )
    }

    Box(
        modifier = modifier
    ) {
        HorizontallyScrollableContainer(
            modifier = Modifier.background(
                color = JewelTheme.textAreaStyle.colors.background,
                shape = RoundedCornerShape(4.dp)
            ).border(
                alignment = Stroke.Alignment.Inside,
                width = 1.dp,
                color = JewelTheme.textAreaStyle.colors.border,
                shape = RoundedCornerShape(4.dp),
            ),
            style = scrollbarStyle,
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                SelectionContainer {
                    when (!content.isNullOrBlank()) {
                        true -> Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = content,
                            style = Typography.consoleTextStyle(),
                        )

                        else -> Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "No content",
                            style = Typography.consoleTextStyle(),
                            color = JewelTheme.colorPalette.gray(6)
                        )
                    }
                }
            }
        }
        if (allowCopy && !content.isNullOrBlank()) {
            var copied by remember { mutableStateOf(false) }
            IconActionButton(
                modifier = Modifier.align(Alignment.TopEnd),
                contentDescription = "Copy to clipboard",
                key = when (copied) {
                    true -> AllIconsKeys.Actions.Checked
                    false -> AllIconsKeys.Actions.Copy
                },
                style = IconButtonStyle(
                    colors = IconButtonColors.dark(
                        backgroundFocused = when (copied) {
                            true -> JewelTheme.colorPalette.green(4)
                            false -> Color.Unspecified
                        },
                    ),
                    metrics = JewelTheme.iconButtonStyle.metrics
                ),
                onClick = {
                    scope.launch {
                        clipboardManager.setText(
                            annotatedString = buildAnnotatedString {
                                append(text = content)
                            }
                        )
                        copied = true
                        delay(2.seconds)
                        copied = false
                    }
                },
                tooltipModifier = Modifier.align(Alignment.TopEnd),
                tooltip = { Text("Copy to clipboard") },
                tooltipStyle = TooltipStyle.light(),
                tooltipPlacement = FixedCursorPoint(offset = DpOffset(0.dp, 16.dp))
            )
        }
    }
}