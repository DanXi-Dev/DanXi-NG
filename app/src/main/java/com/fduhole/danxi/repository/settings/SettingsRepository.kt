package com.fduhole.danxi.repository.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fduhole.danxi.model.fdu.UISInfo
import com.fduhole.danxi.model.opentreehole.OTJWTToken
import com.fduhole.danxi.repository.settings.basic.jsonItem
import com.fduhole.danxi.repository.settings.basic.settingsItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    dataStore: DataStore<Preferences>,
) {
    // high contrast color
    private val keyHighContrastColor = booleanPreferencesKey("high_contrast_color")
    val highContrastColor = settingsItem(keyHighContrastColor, false, dataStore)

    // dark mode
    private val keyDarkTheme = booleanPreferencesKey("dark_theme")
    val darkTheme = settingsItem(keyDarkTheme, null, dataStore)

    /* secret items */
    private val keyFDUUISInfo = stringPreferencesKey("fdu_uis_info")
    val uisInfo = jsonItem<UISInfo>(settingsItem(keyFDUUISInfo, null, dataStore))

    private val keyFDUHoleInfo = stringPreferencesKey("fduhole_token")
    val fduHoleToken = jsonItem<OTJWTToken>(settingsItem(keyFDUHoleInfo, null, dataStore))
}