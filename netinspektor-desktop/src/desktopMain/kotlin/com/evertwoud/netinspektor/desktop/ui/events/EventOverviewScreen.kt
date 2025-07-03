@file:Suppress("UnstableApiUsage")

package com.evertwoud.netinspektor.desktop.ui.events

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.evertwoud.netinspektor.desktop.MainViewModel
import com.evertwoud.netinspektor.desktop.ui.events.component.EventRow
import com.evertwoud.netinspektor.desktop.ui.events.detail.EventDetailScreen
import com.evertwoud.netinspektor.desktop.util.AppControls
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.dark
import org.jetbrains.jewel.intui.standalone.styling.default
import org.jetbrains.jewel.intui.standalone.theme.*
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.component.styling.DividerMetrics
import org.jetbrains.jewel.ui.component.styling.DividerStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarVisibility
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.theme.dividerStyle
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventOverviewScreen(
    controls: AppControls,
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val splitLayoutState = rememberSplitLayoutState(1F / 3F)
    val scrollState = rememberLazyListState()
    val scrollbarStyle = remember {
        val base = ScrollbarStyle.dark()
        ScrollbarStyle(
            colors = base.colors,
            metrics = base.metrics,
            trackClickBehavior = base.trackClickBehavior,
            scrollbarVisibility = ScrollbarVisibility.WhenScrolling.default(),
        )
    }
    var autoScrollEnabled by remember { mutableStateOf(true) }
    var showScrollEffect by remember { mutableStateOf(false) }
    val autoScroll by remember { derivedStateOf { scrollState.canScrollForward && autoScrollEnabled } }

    HorizontalSplitLayout(
        modifier = Modifier.fillMaxSize(),
        firstPaneMinWidth = 156.dp,
        state = splitLayoutState,
        first = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(JewelTheme.globalColors.panelBackground),
            ) {
                viewModel.session?.let { session ->
                    VerticallyScrollableContainer(
                        scrollState = scrollState,
                        style = scrollbarStyle,
                        modifier = Modifier.fillMaxWidth().weight(1F)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onPreviewKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyDown) {
                                        viewModel.selection?.let { selection ->
                                            viewModel.session?.data?.events?.let { list ->
                                                val index = list.indexOf(selection)
                                                when (event.key) {
                                                    Key.DirectionDown -> {
                                                        scope.launch {
                                                            index.plus(1).takeIf { it >= 0 }
                                                                ?.let { newIndex ->
                                                                    viewModel.selection =
                                                                        list.getOrElse(
                                                                            index = newIndex,
                                                                            defaultValue = { selection }
                                                                        )
                                                                    scrollState.animateScrollToItem(
                                                                        newIndex
                                                                    )
                                                                }
                                                        }
                                                        true
                                                    }

                                                    Key.DirectionUp -> {
                                                        scope.launch {
                                                            index.minus(1).takeIf { it >= 0 }
                                                                ?.let { newIndex ->
                                                                    viewModel.selection =
                                                                        list.getOrElse(
                                                                            index = newIndex,
                                                                            defaultValue = { selection }
                                                                        )
                                                                    scrollState.animateScrollToItem(
                                                                        newIndex
                                                                    )
                                                                }
                                                        }
                                                        true
                                                    }

                                                    else -> false
                                                }
                                            }
                                        } ?: false
                                    } else false
                                }
                        ) {
                            LaunchedEffect(session) {
                                scrollState.animateScrollToItem(session.data.events.size)
                            }
                            LaunchedEffect(session.data.events) {
                                if (scrollState.canScrollForward) {
                                    if (autoScroll) scrollState.animateScrollToItem(session.data.events.size)
                                    showScrollEffect = true
                                    delay(0.75.seconds)
                                    showScrollEffect = false
                                }
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = scrollState,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(10.dp)
                            ) {
                                if (!session.data.archive.isEmpty) {
                                    item(key = "restore-archive") {
                                        Chip(
                                            modifier = Modifier.animateItem(),
                                            content = { Text("Restore archive") },
                                            onClick = { session.data.restoreArchive() }
                                        )
                                    }
                                }
                                items(items = session.data.events, key = { it.uuid }) { event ->
                                    EventRow(
                                        modifier = Modifier.fillMaxWidth().animateItem(),
                                        session = session,
                                        event = event,
                                        selected = event == viewModel.selection,
                                        linked = viewModel.linkedEvents.value?.contains(event) == true,
                                    ) {
                                        viewModel.selection = event
                                    }
                                }
                            }

                            this@Column.AnimatedVisibility(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                visible = scrollState.canScrollForward,
                                enter = expandIn(expandFrom = Alignment.BottomCenter) + fadeIn(),
                                exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
                            ) {
                                Chip(
                                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                                    content = { Text("Scroll to bottom") },
                                    onClick = {
                                        scope.launch {
                                            scrollState.animateScrollToItem(session.data.events.size)
                                        }
                                    }
                                )
                            }

                            this@Column.AnimatedVisibility(
                                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                                visible = showScrollEffect,
                                enter = expandIn(expandFrom = Alignment.BottomCenter) + fadeIn(),
                                exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(0.5F)
                                        .height(12.dp)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    JewelTheme.colorPalette.blue(8)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = JewelTheme.globalColors.borders.disabled
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SelectableIconActionButton(
                            key = AllIconsKeys.RunConfigurations.Scroll_down,
                            contentDescription = "Scroll to bottom",
                            selected = autoScrollEnabled,
                            onClick = {
                                scope.launch {
                                    autoScrollEnabled = !autoScrollEnabled
                                    scrollState.animateScrollToItem(session.data.events.size)
                                }
                            },
                        )
                        Spacer(modifier = Modifier.weight(1F))
                        IconButton(
                            onClick = { session.data.archive() },
                            enabled = session.data.events.isNotEmpty()
                        ) {
                            Icon(
                                key = AllIconsKeys.Actions.ClearCash,
                                contentDescription = "Clear"
                            )
                        }
                    }
                }
            }
        },
        dividerStyle = DividerStyle(
            color = JewelTheme.globalColors.borders.disabled,
            metrics = DividerMetrics.defaults()
        ),
        second = {
            IntUiTheme(
                theme = JewelTheme.darkThemeDefinition(
                    defaultTextStyle = JewelTheme.createDefaultTextStyle(),
                    editorTextStyle = JewelTheme.createEditorTextStyle()
                ),
                styling = ComponentStyling.default(),
                swingCompatMode = false,
            ) {
                Box(modifier = Modifier.fillMaxSize().background(JewelTheme.colorPalette.gray(1))) {
                    viewModel.selection?.let { selection ->
                        EventDetailScreen(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = viewModel,
                            controls = controls,
                            event = selection,
                        )
                    }
                }
            }
        }
    )
}