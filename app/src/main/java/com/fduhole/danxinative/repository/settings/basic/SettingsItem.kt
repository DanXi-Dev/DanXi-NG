package com.fduhole.danxinative.repository.settings.basic

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

fun <T> settingsItem(
    key: Preferences.Key<T>,
    initialValue: T?,
    dataStore: DataStore<Preferences>,
): DataStoreItem<T> {
    return object : DataStoreItem<T> {
        override suspend fun set(value: T?) = withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                if (value != null) {
                    preferences[key] = value
                } else {
                    preferences.remove(key)
                }
            }
            return@withContext
        }

        override val flow: Flow<T?>
            get() = dataStore.data.map { preferences ->
                preferences[key] ?: initialValue
            }
    }
}