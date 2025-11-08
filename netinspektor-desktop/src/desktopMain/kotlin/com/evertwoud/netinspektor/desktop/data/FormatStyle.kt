package com.evertwoud.netinspektor.desktop.data

import org.jetbrains.jewel.ui.icon.IntelliJIconKey
import org.jetbrains.jewel.ui.icons.AllIconsKeys

enum class FormatStyle(val label: String, val icon: IntelliJIconKey) {
    Pretty("Pretty", AllIconsKeys.Actions.ReformatCode),
    Minified("Minified", AllIconsKeys.FileTypes.Json),
    Structured("Structured", AllIconsKeys.Toolwindows.ToolWindowStructure),
    Original("Original", AllIconsKeys.FileTypes.Text),
}