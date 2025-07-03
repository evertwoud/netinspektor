package com.evertwoud.netinspektor.desktop.ui.events.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.MainViewModel
import com.evertwoud.netinspektor.desktop.data.FormatStyle
import com.evertwoud.netinspektor.desktop.ext.formatAsTime
import com.evertwoud.netinspektor.desktop.ext.getOrMatchRequest
import com.evertwoud.netinspektor.desktop.ext.getOrMatchResponse
import com.evertwoud.netinspektor.desktop.ext.statusCodeColor
import com.evertwoud.netinspektor.desktop.ui.component.ContentBlock
import com.evertwoud.netinspektor.desktop.util.AppControls
import com.evertwoud.netinspektor.desktop.util.BodyFormatter
import io.ktor.http.HttpStatusCode
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.default
import org.jetbrains.jewel.intui.standalone.styling.light
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.component.styling.ScrollbarStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarVisibility
import org.jetbrains.jewel.ui.theme.colorPalette

@Composable
fun EventDetailScreen(
    modifier: Modifier = Modifier,
    controls: AppControls,
    viewModel: MainViewModel,
    event: NetInspektorEvent,
) {
    val scrollState = rememberScrollState()
    val scrollbarStyle = remember {
        val base = ScrollbarStyle.light()
        ScrollbarStyle(
            colors = base.colors,
            metrics = base.metrics,
            trackClickBehavior = base.trackClickBehavior,
            scrollbarVisibility = ScrollbarVisibility.WhenScrolling.default(),
        )
    }

    VerticallyScrollableContainer(
        scrollState = scrollState,
        style = scrollbarStyle,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            if (event is NetInspektorEvent.Response) {
                event.getOrMatchRequest(viewModel.session)?.let { match ->
                    Link(
                        text ="Go to request",
                        onClick = {
                            viewModel.selection = match
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = when (event) {
                        is NetInspektorEvent.Request -> "Request"
                        is NetInspektorEvent.Response -> "Response"
                    },
                    style = Typography.h1TextStyle(),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.timestamp.formatAsTime(),
                    maxLines = 1,
                    color = JewelTheme.colorPalette.gray(7),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                event.getOrMatchRequest(viewModel.session)?.let { request ->
                    Text(
                        modifier = Modifier.weight(1F),
                        text = request.method,
                        style = Typography.h3TextStyle(),
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                event.getOrMatchResponse(viewModel.session)?.let { response ->
                    Text(
                        modifier = Modifier.clip(shape = RoundedCornerShape(50))
                            .background(response.statusCodeColor(7))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = listOf(
                            response.statusCode,
                            response.statusDescription ?: HttpStatusCode.fromValue(response.statusCode).description
                        ).joinToString(separator = " - "),
                        maxLines = 1,
                        color = response.statusCodeColor(1),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (event is NetInspektorEvent.Request) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "URL",
                )
                Spacer(modifier = Modifier.height(8.dp))
                ContentBlock(
                    modifier = Modifier.fillMaxWidth(),
                    content = event.url
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Headers",
            )
            Spacer(modifier = Modifier.height(8.dp))
            ContentBlock(
                modifier = Modifier.fillMaxWidth(),
                content = event.prettyHeaders
            )
            Spacer(modifier = Modifier.height(16.dp))
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
                        listOf(
                            FormatStyle.Pretty,
                            FormatStyle.Minified,
                            FormatStyle.Original,
                        ).forEach { style ->
                            selectableItem(
                                selected = viewModel.formatStyle == style,
                                onClick = { viewModel.formatStyle = style }
                            ) {
                                Text(style.label)
                            }
                        }
                    },
                    content = {
                        Text(viewModel.formatStyle.label)
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ContentBlock(
                modifier = Modifier.fillMaxWidth(),
                content = when (viewModel.formatStyle) {
                    FormatStyle.Pretty -> BodyFormatter.prettyPrint(input = event.body)
                    FormatStyle.Minified -> BodyFormatter.minified(input = event.body)
                    FormatStyle.Original -> event.body.toString()
                },
            )
        }
    }
}