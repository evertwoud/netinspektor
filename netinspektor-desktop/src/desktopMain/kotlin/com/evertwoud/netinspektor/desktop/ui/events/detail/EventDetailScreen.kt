package com.evertwoud.netinspektor.desktop.ui.events.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.evertwoud.netinspektor.core.event.NetInspektorEvent
import com.evertwoud.netinspektor.desktop.MainViewModel
import com.evertwoud.netinspektor.desktop.ext.*
import com.evertwoud.netinspektor.desktop.ui.component.ContentComponent
import com.evertwoud.netinspektor.desktop.ui.component.KeyValueListComponent
import com.evertwoud.netinspektor.desktop.ui.component.body.JsonBodyComponent
import com.evertwoud.netinspektor.desktop.util.AppControls
import io.ktor.http.*
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.dark
import org.jetbrains.jewel.intui.standalone.styling.default
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.component.styling.ScrollbarStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarVisibility
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.theme.textAreaStyle

@OptIn(ExperimentalFoundationApi::class, ExperimentalJewelApi::class)
@Composable
fun EventDetailScreen(
    modifier: Modifier = Modifier,
    controls: AppControls,
    viewModel: MainViewModel,
    event: NetInspektorEvent,
) {
    val clipboardManager = LocalClipboardManager.current
    val scrollState = remember(event) { ScrollState(0) }
    val scrollbarStyle = remember {
        val base = ScrollbarStyle.dark()
        ScrollbarStyle(
            colors = base.colors,
            metrics = base.metrics,
            trackClickBehavior = base.trackClickBehavior,
            scrollbarVisibility = ScrollbarVisibility.WhenScrolling.default(),
        )
    }

    val contentType = remember(event) { event.body?.contentType?.let { ContentType.parse(it) } }
    val content = remember(event) { event.body?.data?.decodeToString() }

    VerticallyScrollableContainer(
        scrollState = scrollState,
        style = scrollbarStyle,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(end = scrollbarContentSafePadding())
        ) {

            if (event is NetInspektorEvent.Response) {
                event.getOrMatchRequest(viewModel.session)?.let { match ->
                    Link(
                        text = "Go to request",
                        onClick = {
                            viewModel.selection = match
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
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
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            SelectionContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (event is NetInspektorEvent.Response) {
                        val duration = remember(event) {
                            val request = event.getOrMatchRequest(viewModel.session) ?: return@remember null
                            return@remember event.timestamp - request.timestamp
                        }
                        duration?.let {
                            SelectionContainer {
                                Text(
                                    text = it.formatAsDuration(),
                                    maxLines = 1,
                                    color = JewelTheme.colorPalette.gray(7),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    SelectionContainer {
                        Text(
                            text = event.timestamp.formatAsTimeStamp(),
                            maxLines = 1,
                            color = JewelTheme.colorPalette.gray(7),
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            event.getOrMatchRequest(viewModel.session)?.let { request ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "URL",
                )
                Spacer(modifier = Modifier.height(8.dp))
                KeyValueListComponent(
                    modifier = Modifier.fillMaxWidth(),
                    content = mapOf(request.method to request.url),
                    divider = " ",
                    allowCopy = false
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Headers",
            )
            Spacer(modifier = Modifier.height(8.dp))
            KeyValueListComponent(
                modifier = Modifier.fillMaxWidth(),
                content = event.headers
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Determine body
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Content type: ${contentType ?: "unknown"}"
            )
            Spacer(modifier = Modifier.height(8.dp))
            when {
                contentType?.match(ContentType.Application.Json) == true -> JsonBodyComponent(
                    viewModel = viewModel,
                    body = event.body
                )

                else -> ContentComponent {
                    Text(
                        modifier = Modifier,
                        text = content ?: "No content",
                        style = JewelTheme.editorTextStyle,
                        color = when (content.isNullOrEmpty()) {
                            true -> JewelTheme.textAreaStyle.colors.contentDisabled
                            else -> Color.Unspecified
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.weight(1F))
                if (event is NetInspektorEvent.Request) {
                    OutlinedButton(
                        content = { Text("Copy cURL") },
                        onClick = {
                            clipboardManager.setText(AnnotatedString(event.toCurlRequest()))

                        }
                    )
                }
                OutlinedButton(
                    content = { Text("Copy to clipboard") },
                    onClick = {
                        clipboardManager.setText(
                            when (event) {
                                is NetInspektorEvent.Request -> buildAnnotatedString {
                                    append("🔗 Endpoint:\n")
                                    append(event.method)
                                    append(" ")
                                    append(event.url)
                                    event.getOrMatchResponse(viewModel.session)?.let { match ->
                                        append("\n\n⚙️ Status:\n")
                                        append(match.statusCode.toString())
                                    }
                                    append("\n\n📋 Headers:\n")
                                    append(event.prettyHeaders)
                                    event.body?.data?.decodeToString()?.takeIf { it.isNotEmpty() }?.let { content ->
                                        append("\n\n📥 Body:\n")
                                        append(text = content)
                                    }
                                }

                                is NetInspektorEvent.Response -> buildAnnotatedString {
                                    event.getOrMatchRequest(viewModel.session)?.let { match ->
                                        append("🔗 Endpoint:\n")
                                        append(match.method)
                                        append(" ")
                                        append(match.url)
                                    }
                                    append("\n\n⚙️ Status:\n")
                                    append(event.statusCode.toString())
                                    append("\n\n📋 Headers:\n")
                                    append(event.prettyHeaders)
                                    event.body?.data?.decodeToString()?.takeIf { it.isNotEmpty() }?.let { content ->
                                        append("\n\n📥 Body:\n")
                                        append(text = content)
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}