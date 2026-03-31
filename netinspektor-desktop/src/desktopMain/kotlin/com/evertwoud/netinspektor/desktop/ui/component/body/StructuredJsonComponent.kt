@file:OptIn(ExperimentalFoundationApi::class, ExperimentalJewelApi::class)

package com.evertwoud.netinspektor.desktop.ui.component.body

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.evertwoud.netinspektor.desktop.ui.component.ContentComponent
import com.evertwoud.netinspektor.desktop.ui.component.KeyValueComponent
import com.evertwoud.netinspektor.desktop.util.FormatConstants
import kotlinx.serialization.json.*
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.light
import org.jetbrains.jewel.ui.component.FixedCursorPoint
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.styling.TooltipStyle
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.theme.textAreaStyle

private typealias ExpansionStateMap = MutableMap<String, Boolean>

@Composable
fun StructuredJsonComponent(
    modifier: Modifier,
    value: String
) {
    val clipboardManager = LocalClipboardManager.current

    val structure = remember(value) {
        try {
            Json.decodeFromString<JsonElement?>(value)
        } catch (e: Exception) {
            null
        }
    }
    val expansionStates = remember(value) {
        mutableStateMapOf<String, Boolean>().apply { put("root", FormatConstants.EXPANDED_BY_DEFAULT) }
    }
    val collapsiblePaths = remember(structure) {
        structure.collectCollapsiblePaths(currentPath = "root")
    }

    // Helper function to update the map, wrapped in a lambda to be passed down
    val onToggle: (String) -> Unit = remember(expansionStates) {
        { path ->
            expansionStates[path] = !(expansionStates[path] ?: FormatConstants.EXPANDED_BY_DEFAULT)
        }
    }
    val onExpandAll = remember(expansionStates, collapsiblePaths) {
        {
            expansionStates["root"] = true
            collapsiblePaths.forEach { path -> expansionStates[path] = true }
        }
    }
    val onCollapseAll = remember(expansionStates, collapsiblePaths) {
        {
            expansionStates["root"] = false
            collapsiblePaths.forEach { path -> expansionStates[path] = false }
        }
    }

    SelectionContainer {
        Box(modifier) {
            ContentComponent {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when {
                        structure != null -> BuildJsonContent(
                            key = when (structure) {
                                is JsonArray -> "array"
                                is JsonObject -> "object"
                                else -> "null"
                            },
                            node = structure,
                            currentPath = "root", // Start path
                            expansionStates = expansionStates,
                            onToggle = onToggle
                        )

                        else -> Text(
                            text = "Not supported",
                            style = JewelTheme.editorTextStyle,
                            color = JewelTheme.textAreaStyle.colors.contentDisabled
                        )
                    }
                }
            }
            if (value.isNotBlank()) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    IconActionButton(
                        contentDescription = "Expand all",
                        key = AllIconsKeys.Actions.Expandall,
                        onClick = onExpandAll,
                        tooltip = { Text("Expand all") },
                        tooltipStyle = TooltipStyle.light(),
                        tooltipPlacement = FixedCursorPoint(offset = DpOffset(0.dp, 4.dp))
                    )
                    IconActionButton(
                        contentDescription = "Collapse all",
                        key = AllIconsKeys.Actions.Collapseall,
                        onClick = onCollapseAll,
                        tooltip = { Text("Collapse all") },
                        tooltipStyle = TooltipStyle.light(),
                        tooltipPlacement = FixedCursorPoint(offset = DpOffset(0.dp, 4.dp))
                    )
                    IconActionButton(
                        contentDescription = "Copy to clipboard",
                        key = AllIconsKeys.Actions.Copy,
                        onClick = { clipboardManager.setText(annotatedString = AnnotatedString(text = value)) },
                        tooltip = { Text("Copy to clipboard") },
                        tooltipStyle = TooltipStyle.light(),
                        tooltipPlacement = FixedCursorPoint(offset = DpOffset(0.dp, 4.dp))
                    )
                }

            }
        }
    }
}

@Composable
private fun ParentNode(
    modifier: Modifier = Modifier,
    key: String,
    sizeLabel: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    isCollapsible: Boolean
) {
    Row(
        modifier = modifier.then(if (isCollapsible) Modifier.clickable(onClick = onToggle) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCollapsible) {
            Icon(
                if (isExpanded) AllIconsKeys.General.ChevronDown else AllIconsKeys.General.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(4.dp))
        }

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = JewelTheme.colorPalette.yellow(8))) {
                    append(key)
                }
                withStyle(SpanStyle(color = JewelTheme.colorPalette.gray(8))) {
                    append(sizeLabel)
                }
            },
            fontFamily = JewelTheme.editorTextStyle.fontFamily
        )
    }
}

@Composable
private fun BuildJsonContent(
    modifier: Modifier = Modifier,
    key: String,
    node: JsonElement?,
    currentPath: String,
    expansionStates: ExpansionStateMap,
    onToggle: (String) -> Unit
) {
    val isExpanded = expansionStates[currentPath] ?: FormatConstants.EXPANDED_BY_DEFAULT

    when (node) {
        is JsonArray -> {
            val isCollapsible = remember(node) { node.isNotEmpty() }
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ParentNode(
                    key = key,
                    sizeLabel = " [${node.size}]",
                    isExpanded = isExpanded,
                    onToggle = { onToggle(currentPath) },
                    isCollapsible = isCollapsible
                )

                if (isCollapsible) {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically(animationSpec = tween(150)),
                        exit = shrinkVertically(animationSpec = tween(150))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            node.forEachIndexed { index, sub ->
                                BuildJsonContent(
                                    modifier = Modifier.padding(start = 20.dp),
                                    key = "$index",
                                    node = sub,
                                    currentPath = "$currentPath/$index",
                                    expansionStates = expansionStates,
                                    onToggle = onToggle
                                )
                            }
                        }
                    }
                }
            }
        }

        is JsonObject -> {
            val isCollapsible = remember(node) { node.isNotEmpty() }
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ParentNode(
                    key = key,
                    sizeLabel = " {${node.size}}",
                    isExpanded = isExpanded,
                    onToggle = { onToggle(currentPath) },
                    isCollapsible = isCollapsible,
                )

                if (isCollapsible) {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically(animationSpec = tween(150)),
                        exit = shrinkVertically(animationSpec = tween(150))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            node.forEach { (subKey, subValue) ->
                                BuildJsonContent(
                                    modifier = Modifier.padding(start = 20.dp),
                                    key = subKey,
                                    node = subValue,
                                    currentPath = "$currentPath/$subKey",
                                    expansionStates = expansionStates,
                                    onToggle = onToggle
                                )
                            }
                        }
                    }
                }
            }
        }

        is JsonPrimitive -> KeyValueComponent(
            modifier = modifier,
            key = key,
            value = node.content,
            divider = ": ",
        )

        else -> Unit
    }
}

private fun JsonElement?.collectCollapsiblePaths(currentPath: String): Set<String> {
    if (this == null) return emptySet()

    val paths = mutableSetOf<String>()

    when (this) {
        is JsonArray -> {
            if (isNotEmpty()) {
                paths += currentPath
                forEachIndexed { index, child ->
                    paths += child.collectCollapsiblePaths(currentPath = "$currentPath/$index")
                }
            }
        }

        is JsonObject -> {
            if (isNotEmpty()) {
                paths += currentPath
                forEach { (key, child) ->
                    paths += child.collectCollapsiblePaths(currentPath = "$currentPath/$key")
                }
            }
        }

        else -> Unit
    }

    return paths
}

