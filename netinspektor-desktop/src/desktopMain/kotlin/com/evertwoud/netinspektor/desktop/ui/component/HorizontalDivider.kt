package com.evertwoud.netinspektor.desktop.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier.fillMaxWidth(),
    height: Dp = 2.dp,
    color: Color = JewelTheme.globalColors.borders.disabled
) {
    Box(
        modifier = modifier.height(height).background(color),
    )
}