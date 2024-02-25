package com.fduhole.danxi.util.net

import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url

open class MemoryCookiesStorage(
    private var storage: CookiesStorage = AcceptAllCookiesStorage(),
) : CookiesStorage {
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) = storage.addCookie(requestUrl, cookie)

    override fun close() = storage.close()

    override suspend fun get(requestUrl: Url) = storage.get(requestUrl)

    fun replaceBy(storage: CookiesStorage) {
        this.storage = storage
    }
}