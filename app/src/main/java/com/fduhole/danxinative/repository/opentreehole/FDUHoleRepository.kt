@file:OptIn(ExperimentalSerializationApi::class)

package com.fduhole.danxinative.repository.opentreehole

import com.fduhole.danxinative.model.opentreehole.OTJWTToken
import com.fduhole.danxinative.model.opentreehole.OTLoginInfo
import com.fduhole.danxinative.repository.BaseRepository
import com.fduhole.danxinative.state.GlobalState
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Inject

// Configure the JSON (de)serializer to match APIs' need better.
private val jsonConfig = Json {
    // Do not throw an exception when deserializing an object with unknown keys.
    ignoreUnknownKeys = true
    // Do not encode a field into the final json string if it is null.
    explicitNulls = false
}

class FDUHoleRepository @Inject constructor(
    globalState: GlobalState
) : BaseRepository(globalState) {
    companion object {
        const val BASE_URL = "https://hole.hath.top/api/"
        const val BASE_AUTH_URL = "https://testauth.hath.top/api/"
    }

    private val authApiService: FDUHoleAuthApiService =
        Ktorfit.Builder().httpClient(client).baseUrl(BASE_AUTH_URL).build().create()

    override fun createClient() = createTmpClient {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }


    override val scopeId = "fduhole.com"

    suspend fun login(email: String, password: String): OTJWTToken = authApiService.login(OTLoginInfo(password, email))
}