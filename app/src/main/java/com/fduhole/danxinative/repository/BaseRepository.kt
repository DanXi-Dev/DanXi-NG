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

    val cookieJar: CookieJar
        get() {
            if (!cookieJars.containsKey(getHost())) {
                cookieJars[getHost()] = JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL))
            }
            return cookieJars[getHost()]!!
        }
    val client: OkHttpClient
        get() {
            if (!clients.containsKey(getHost())) {
                clients[getHost()] = OkHttpClient.Builder().cookieJar(cookieJar).build()
            }
            return clients[getHost()]!!
        }

    abstract fun getHost(): String
}