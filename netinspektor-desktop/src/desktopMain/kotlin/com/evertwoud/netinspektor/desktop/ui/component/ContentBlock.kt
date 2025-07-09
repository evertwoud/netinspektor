@file:Suppress("UnstableApiUsage")

package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
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

    Box(
        modifier = modifier
    ) {
        TextArea(
            modifier = Modifier.fillMaxWidth(),
            value = when (!content.isNullOrBlank()) {
                true -> content
                else -> "No content"
            },
            decorationBoxModifier = Modifier.padding(12.dp),
            onValueChange = {},
            enabled = !content.isNullOrBlank(),
            readOnly = true,
        )
        if (allowCopy && !content.isNullOrBlank()) {
            IconActionButton(
                modifier = Modifier.align(Alignment.TopEnd),
                contentDescription = "Copy to clipboard",
                key = AllIconsKeys.Actions.Copy,
                onClick = {
                    clipboardManager.setText(
                        annotatedString = buildAnnotatedString {
                            append(text = content)
                        }
                    )
                },
                tooltipModifier = Modifier.align(Alignment.TopEnd),
                tooltip = { Text("Copy to clipboard") },
                tooltipStyle = TooltipStyle.light(),
                tooltipPlacement = FixedCursorPoint(offset = DpOffset(0.dp, 16.dp))
            )
        }
    }
}