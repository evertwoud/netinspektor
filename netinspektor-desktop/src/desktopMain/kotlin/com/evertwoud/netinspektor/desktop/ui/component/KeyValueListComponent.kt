package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.theme.textAreaStyle

@Composable
fun KeyValueListComponent(
    modifier: Modifier,
    content: Map<String, String>,
    divider: String = ": "
) {
    SelectionContainer {
        ContentComponent {
            Column(
                modifier = modifier,
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
    }
}
