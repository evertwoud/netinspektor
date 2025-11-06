@file:Suppress("UnstableApiUsage")

package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.intui.standalone.styling.light
import org.jetbrains.jewel.ui.component.FixedCursorPoint
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextArea
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.component.styling.TooltipStyle
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@OptIn(ExperimentalFoundationApi::class, ExperimentalJewelApi::class)
@Composable
fun ContentBlock(
    modifier: Modifier = Modifier,
    content: String?,
    allowCopy: Boolean = true,
) {
    val clipboardManager = LocalClipboardManager.current
    var value by remember(content) {
        mutableStateOf(
            value = TextFieldValue(
                text = when (!content.isNullOrBlank()) {
                    true -> content
                    else -> "No content"
                },
            )
        )
    }
    Box(
        modifier = modifier
    ) {
        TextArea(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            decorationBoxModifier = Modifier.padding(12.dp),
            onValueChange = { value = it },
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