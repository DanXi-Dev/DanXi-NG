package com.fduhole.danxinative.repository

import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.net.MemoryCookiesStorage
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies


abstract class BaseRepository(
    val globalState: GlobalState
) {
    companion object {
        private val clients: MutableMap<String, HttpClient> = mutableMapOf()
        private val cookiesStorages: MutableMap<String, MemoryCookiesStorage> = mutableMapOf()

        fun createTmpClient(
            cookiesStorage: CookiesStorage = AcceptAllCookiesStorage(),
            block: HttpClientConfig<CIOEngineConfig>.() -> Unit = {},
        ) = HttpClient(CIO) {
            engine {
                endpoint {
                    connectTimeout = 50_000
                    requestTimeout = 50_000
                }
            }
            install(HttpCookies) {
                storage = cookiesStorage
            }
            install(HttpRedirect) {
                checkHttpMethod = false // not work with CIO
            }
            block()
        }
    }

    val cookiesStorage: MemoryCookiesStorage
        get() {
            if (!cookiesStorages.containsKey(scopeId)) {
                cookiesStorages[scopeId] = MemoryCookiesStorage(AcceptAllCookiesStorage())
            }
            return cookiesStorages[scopeId]!!
        }

    val client: HttpClient
        get() {
            if (!clients.containsKey(scopeId)) {
                clients[scopeId] = createClient()
            }
            return clients[scopeId]!!
        }

    open fun createClient(): HttpClient = createTmpClient(cookiesStorage)


    /**
     * Get the scope id of the repository.
     *
     * The repositories with the same id will share one client and cookie jar.
     */
    abstract val scopeId: String
}