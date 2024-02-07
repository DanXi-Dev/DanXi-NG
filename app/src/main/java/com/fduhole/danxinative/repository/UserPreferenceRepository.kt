package com.fduhole.danxinative.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fduhole.danxinative.model.opentreehole.OTJWTToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class FDUUISInfo(
    val id: String,
    val password: String,
) {
    fun isEmpty() = id.isEmpty() || password.isEmpty()
}

val emptyFDUUISInfo = FDUUISInfo("", "")

class UserPreferenceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        const val KEY_HIGH_CONTRAST_COLOR = "high_contrast_color"
        const val KEY_DARK_THEME = "dark_theme"

        const val KEY_FDU_UIS_Info = "fdu_uis_info"
        const val KEY_FDUHOLE_TOKEN = "fduhole_token"
    }

    // high contrast color
    private val keyHighContrastColor = booleanPreferencesKey(KEY_HIGH_CONTRAST_COLOR)
    val highContrastColor: Flow<Boolean>
        get() = dataStore.data.map { it[keyHighContrastColor] ?: false }

    suspend fun setHighContrastColor(value: Boolean) =
        dataStore.edit { it[keyHighContrastColor] = value }

    // dark mode
    private val keyDarkTheme = intPreferencesKey(KEY_DARK_THEME)
    val isDarkTheme: Flow<Boolean?>
        get() = dataStore.data.map {
            it[keyDarkTheme]?.run {
                when (this) {
                    2 -> true
                    1 -> false
                    else -> null
                }
            }
        }

    suspend fun setDarkTheme(value: Boolean?) = dataStore.edit {
        it[keyDarkTheme] = when (value) {
            true -> 2
            false -> 1
            null -> 0
        }
    }

    private val keyFDUUISInfo = stringPreferencesKey(KEY_FDU_UIS_Info)

    val fduUISInfo: Flow<FDUUISInfo>
        get() = getJson<FDUUISInfo>(KEY_FDU_UIS_Info).map { it ?: emptyFDUUISInfo }

    suspend fun setFDUUISInfo(info: FDUUISInfo) = withContext(Dispatchers.IO) {
        setJson(KEY_FDU_UIS_Info, info)
    }

    val fduHoleToken: Flow<OTJWTToken?>
        get() = getJson(KEY_FDUHOLE_TOKEN)

    suspend fun setFDUHoleToken(token: OTJWTToken) = withContext(Dispatchers.IO) {
        setJson(KEY_FDUHOLE_TOKEN, token)
    }

    private inline fun <reified T> getJson(key: String): Flow<T?> {
        val stringKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[stringKey]?.let {
                Json.decodeFromString(it)
            }
        }
    }

    private suspend inline fun <reified T> setJson(key: String, value: T) {
        val stringKey = stringPreferencesKey(key)
        dataStore.edit { it[stringKey] = Json.encodeToString(value) }
    }
}