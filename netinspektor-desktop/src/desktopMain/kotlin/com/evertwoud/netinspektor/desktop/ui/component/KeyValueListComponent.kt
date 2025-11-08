@file:OptIn(ExperimentalFoundationApi::class, ExperimentalJewelApi::class)

package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.light
import org.jetbrains.jewel.ui.component.FixedCursorPoint
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.styling.TooltipStyle
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.theme.textAreaStyle

@Composable
fun KeyValueListComponent(
    modifier: Modifier,
    content: Map<String, String>,
    divider: String = ": ",
    allowCopy: Boolean = true,
) {
    val clipboardManager = LocalClipboardManager.current
    Box(modifier) {
        SelectionContainer {
            ContentComponent {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when {
                        content.isNotEmpty() -> content.forEach { (key, value) ->
                            KeyValueComponent(key = key, value = value, divider = divider)
                        }

                        else -> Text(
                            text = "No content",
                            style = JewelTheme.editorTextStyle,
                            color = JewelTheme.textAreaStyle.colors.contentDisabled
                        )
                    }
                }
            }
            if (content.isNotEmpty() && allowCopy) {
                IconActionButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    contentDescription = "Copy to clipboard",
                    key = AllIconsKeys.Actions.Copy,
                    onClick = {
                        clipboardManager.setText(annotatedString = buildAnnotatedString {
                            content.forEach { (key, value) ->
                                appendLine("$key$divider$value")
                            }
                        })
                    },
                    tooltipModifier = Modifier.align(Alignment.TopEnd),
                    tooltip = { Text("Copy to clipboard") },
                    tooltipStyle = TooltipStyle.light(),
                    tooltipPlacement = FixedCursorPoint(offset = DpOffset(0.dp, 4.dp))
                )
            }
        }
    }
}
