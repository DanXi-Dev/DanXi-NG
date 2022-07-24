package com.fduhole.danxinative.util.net

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.JavaNetCookieJar

/// A cookie jar stored in memory, supporting replacing its content with another cookieJar.
class MemoryCookieJar(var javaNetCookieJar: JavaNetCookieJar) : CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> = javaNetCookieJar.loadForRequest(url)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) = javaNetCookieJar.saveFromResponse(url, cookies)

    fun replaceBy(javaNetCookieJar: JavaNetCookieJar) {
        this.javaNetCookieJar = javaNetCookieJar
    }

}