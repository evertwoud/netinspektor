package com.evertwoud.netinspektor.desktop.ui.component.body

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.MainViewModel
import com.evertwoud.netinspektor.desktop.data.FormatStyle
import com.evertwoud.netinspektor.desktop.ui.component.ContentComponent
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.theme.textAreaStyle

@Composable
fun JsonBodyComponent(
    viewModel: MainViewModel,
    body: NetInspektorEvent.Body?
) {
    val formatStyle = viewModel.settings.formatStyle.collectAsState(FormatStyle.Original).value
    val bodyContent = remember { body?.data?.contentToString().orEmpty() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            modifier = Modifier.weight(1F),
            text = "Body",
        )
        Dropdown(
            menuContent = {
                FormatStyle.entries.forEach { style ->
                    selectableItem(
                        selected = formatStyle == style,
                        onClick = { viewModel.settings.setFormatStyle(style) }
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                key = style.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(style.label)
                        }
                    }
                }
            },
            content = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        key = formatStyle.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(formatStyle.label)
                }

            }
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    when (formatStyle) {
        FormatStyle.Structured -> StructuredJsonComponent(
            modifier = Modifier.fillMaxWidth(),
            value = bodyContent,
        )

        else -> ContentComponent(
            modifier = Modifier.fillMaxWidth(),
        ) {
            when {
                bodyContent.isNotEmpty() -> Text(
                    modifier = Modifier,
                    text = bodyContent,
                    style = JewelTheme.editorTextStyle,
                    color = JewelTheme.textAreaStyle.colors.content
                )

                else -> Text(
                    modifier = Modifier,
                    text = "No content",
                    style = JewelTheme.editorTextStyle,
                    color = JewelTheme.textAreaStyle.colors.contentDisabled
                )
            }
        }
    }
}