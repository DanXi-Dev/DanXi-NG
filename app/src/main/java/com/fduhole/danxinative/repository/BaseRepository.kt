package com.fduhole.danxinative.repository

import com.fduhole.danxinative.util.net.MemoryCookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy


abstract class BaseRepository {
    companion object {
        val clients: MutableMap<String, OkHttpClient> = mutableMapOf()
        val cookieJars: MutableMap<String, MemoryCookieJar> = mutableMapOf()
    }

    val cookieJar: MemoryCookieJar
        get() {
            if (!cookieJars.containsKey(getScopeId())) {
                cookieJars[getScopeId()] = MemoryCookieJar(JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL)))
            }
            return cookieJars[getScopeId()]!!
        }

    var client: OkHttpClient
        get() {
            if (!clients.containsKey(getScopeId())) {
                client = clientFactory().build()
            }
            return clients[getScopeId()]!!
        }
        set(value) {
            clients[getScopeId()] = value
        }

    open fun clientFactory(): OkHttpClient.Builder = clientFactoryNoCookie().cookieJar(cookieJar)

    fun clientFactoryNoCookie(): OkHttpClient.Builder = OkHttpClient.Builder().cache(null)

    /**
     * Get the scope id of the repository.
     *
     * The repositories with the same id will share one client and cookie jar.
     */
    abstract fun getScopeId(): String
}