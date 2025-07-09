package com.evertwoud.netinspektor.desktop.ui.events.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.data.session.SessionClient
import com.evertwoud.netinspektor.desktop.ext.formatAsTime
import com.evertwoud.netinspektor.desktop.ext.getOrMatchRequest
import com.evertwoud.netinspektor.desktop.ext.getOrMatchResponse
import com.evertwoud.netinspektor.desktop.ext.statusCodeColor
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.Stroke
import org.jetbrains.jewel.foundation.modifier.border
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Chip
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.painterResource
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.painter.hints.Size
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.skiko.Cursor

@Composable
fun EventRow(
    modifier: Modifier = Modifier,
    session: SessionClient?,
    event: NetInspektorEvent,
    selected: Boolean,
    linked: Boolean,
    onSelected: (NetInspektorEvent) -> Unit,
) {
    val shape = RoundedCornerShape(4.dp)
    val borderColor = when {
        selected -> JewelTheme.colorPalette.blue(9)
        linked -> JewelTheme.colorPalette.gray(5)
        else -> JewelTheme.colorPalette.gray(3)
    }
    val borderWidth by animateDpAsState(
        when {
            selected || linked -> 2.dp
            else -> 0.dp
        }
    )
    val backgroundColor = when {
        selected -> JewelTheme.colorPalette.gray(4)
        else -> JewelTheme.colorPalette.gray(3)
    }

    Column(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = shape
            )
            .selectable(
                selected = selected,
                onClick = { onSelected(event) }
            )
            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
            .border(
                alignment = Stroke.Alignment.Inside,
                width = borderWidth,
                color = borderColor,
                shape = shape,
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.size(12.dp),
                painter = painterResource(
                    resourcePath = when (event) {
                        is NetInspektorEvent.Request -> AllIconsKeys.Actions.MoveUp.newUiPath
                        is NetInspektorEvent.Response -> AllIconsKeys.Actions.MoveDown.newUiPath
                    }
                ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = when (event) {
                        is NetInspektorEvent.Request -> JewelTheme.colorPalette.blue(7)
                        is NetInspektorEvent.Response -> event.statusCodeColor(7)
                    }
                )
            )
            event.getOrMatchRequest(session)?.let { request ->
                Text(
                    text = request.method,
                    fontSize = 11.sp,
                )
            }
            event.getOrMatchResponse(session)?.let { response ->
                Text(
                    text = listOf(
                        response.statusCode,
                        response.statusDescription ?: HttpStatusCode.fromValue(response.statusCode).description
                    ).joinToString(separator = " - "),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp,
                    color = JewelTheme.colorPalette.gray(9),
                )
            }

            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = event.timestamp.formatAsTime(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = JewelTheme.colorPalette.gray(8),
                fontSize = 11.sp
            )
        }
        AnimatedContent(
            targetState = event.getOrMatchRequest(session),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { request ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = when (request != null) {
                    true -> request.url
                    false -> "Unknown request"
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = when (request != null) {
                    true -> Color.Unspecified
                    false -> JewelTheme.colorPalette.gray(9)
                }
            )
        }
    }
}


