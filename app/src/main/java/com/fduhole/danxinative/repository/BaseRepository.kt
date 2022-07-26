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
            if (!cookieJars.containsKey(getHost())) {
                cookieJars[getHost()] = MemoryCookieJar(JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL)))
            }
            return cookieJars[getHost()]!!
        }

    var client: OkHttpClient
        get() {
            if (!clients.containsKey(getHost())) {
                client = clientFactory().build()
            }
            return clients[getHost()]!!
        }
        set(value) {
            clients[getHost()] = value
        }

    open fun clientFactory(): OkHttpClient.Builder {
        return clientFactoryNoCookie().cookieJar(cookieJar)
    }

    fun clientFactoryNoCookie(): OkHttpClient.Builder {
        return OkHttpClient.Builder().cache(null)
    }

    abstract fun getHost(): String
}