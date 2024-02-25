package com.fduhole.danxi.repository.settings.basic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> jsonItem(parentItem: DataStoreItem<String>): DataStoreItem<T> {
    return object : DataStoreItem<T> {
        override val flow: Flow<T?>
            get() = parentItem.flow.map {
                try {
                    it?.let {
                        Json.decodeFromString(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

        override suspend fun set(value: T?) = withContext(Dispatchers.IO) {
            parentItem.set(value?.let {
                Json.encodeToString(it)
            })
        }
    }
}