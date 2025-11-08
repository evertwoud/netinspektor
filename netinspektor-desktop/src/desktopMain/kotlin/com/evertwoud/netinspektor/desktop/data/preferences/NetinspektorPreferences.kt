package com.evertwoud.netinspektor.desktop.data.preferences

import com.evertwoud.netinspektor.desktop.data.FormatStyle
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

@OptIn(ExperimentalSettingsApi::class)
data class NetinspektorPreferences(
    val settings: PreferencesSettings = PreferencesSettings(Preferences.userRoot())
) {
    val alwaysOnTop: Flow<Boolean> = settings.getBooleanFlow(KEY_ALWAYS_ON_TOP, false)

    fun setAlwaysOnTop(value: Boolean) {
        settings[KEY_ALWAYS_ON_TOP] = value
    }

    val formatStyle: Flow<FormatStyle> = settings.getStringOrNullFlow(KEY_FORMAT_STYLE).map {
        FormatStyle.from(it)
    }

    fun setFormatStyle(value: FormatStyle) {
        settings[KEY_FORMAT_STYLE] = value.name
    }

    companion object {
        private const val KEY_ALWAYS_ON_TOP = "always_on_top"
        private const val KEY_FORMAT_STYLE = "format_style"
    }
}