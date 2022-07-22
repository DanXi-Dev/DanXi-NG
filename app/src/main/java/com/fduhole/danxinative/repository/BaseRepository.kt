package com.fduhole.danxinative.repository

import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy


abstract class BaseRepository {
    companion object {
        val clients: MutableMap<String, OkHttpClient> = mutableMapOf()
        val cookieJars: MutableMap<String, CookieJar> = mutableMapOf()
    }

    var cookieJar: CookieJar
        get() {
            if (!cookieJars.containsKey(getHost())) {
                cookieJar = JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL))
            }
            return cookieJars[getHost()]!!
        }
        set(value) {
            cookieJars[getHost()] = value
            client = clientFactory().build()
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
        return OkHttpClient.Builder().cookieJar(cookieJar).cache(null)
    }

    abstract fun getHost(): String
}