package com.fduhole.danxi.repository.settings.basic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * A simple data store item
 * @see <a href="https://github.com/BIT101-dev/BIT101-Android/blob/main/app/src/main/java/cn/bit101/android/datastore/basic/DataStoreItem.kt">DataStoreItem</a> from BIT101
 */
interface DataStoreItem<T> {
    suspend fun get() = withContext(Dispatchers.IO) { flow.first() }
    suspend fun set(value: T?)
    suspend fun remove() = set(null)
    val flow: Flow<T?>
}