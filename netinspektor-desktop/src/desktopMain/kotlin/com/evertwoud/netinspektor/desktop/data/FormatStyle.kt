package com.evertwoud.netinspektor.desktop.data

import org.jetbrains.jewel.ui.icon.IntelliJIconKey
import org.jetbrains.jewel.ui.icons.AllIconsKeys

enum class FormatStyle(val label: String, val icon: IntelliJIconKey) {
    Original("Original", AllIconsKeys.FileTypes.Text),
    Pretty("Pretty", AllIconsKeys.Actions.ReformatCode),
    Minified("Minified", AllIconsKeys.FileTypes.Json),
    Structured("Structured", AllIconsKeys.Toolwindows.ToolWindowStructure);

    companion object {
        fun from(name: String?) = FormatStyle.entries.firstOrNull {
            it.name.equals(name, true)
        } ?: Original
    }
}