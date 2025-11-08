package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.theme.colorPalette

@Composable
fun KeyValueComponent(
    modifier: Modifier = Modifier,
    key: String,
    value: String,
    divider: String,
    keyColor: Color = JewelTheme.colorPalette.gray(8),
    valueColor: Color = JewelTheme.contentColor
) {
    Row(modifier) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = keyColor)) {
                    append(key)
                    append(divider)
                }
                withStyle(SpanStyle(color = valueColor)) {
                    append(value)
                }
            },
            fontFamily = JewelTheme.editorTextStyle.fontFamily,
        )
    }
}
