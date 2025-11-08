package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.Stroke
import org.jetbrains.jewel.foundation.modifier.border
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.theme.textAreaStyle

@Composable
fun ContentComponent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .background(
                color = JewelTheme.globalColors.panelBackground,
                shape = RoundedCornerShape(8.dp)
            ).border(
                alignment = Stroke.Alignment.Center,
                width = JewelTheme.textAreaStyle.metrics.borderWidth,
                color = JewelTheme.textAreaStyle.colors.border,
                shape = RoundedCornerShape(JewelTheme.textAreaStyle.metrics.cornerSize),
            ),
        content = content
    )
}