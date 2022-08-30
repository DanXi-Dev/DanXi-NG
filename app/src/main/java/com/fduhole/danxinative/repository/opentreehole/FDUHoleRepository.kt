@file:OptIn(ExperimentalSerializationApi::class)

package com.fduhole.danxinative.repository.opentreehole

import com.fduhole.danxinative.model.opentreehole.OTJWTToken
import com.fduhole.danxinative.model.opentreehole.OTLoginInfo
import com.fduhole.danxinative.repository.BaseRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

// Configure the JSON (de)serializer to match APIs' need better.
private val json = Json {
    // Do not throw an exception when deserializing an object with unknown keys.
    ignoreUnknownKeys = true
    // Do not encode a field into the final json string if it is null.
    explicitNulls = false
}

class FDUHoleRepository : BaseRepository() {
    companion object {
        const val BASE_URL = "https://hole.hath.top/api/"
        const val BASE_AUTH_URL = "https://testauth.hath.top/api/"
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_AUTH_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client).build()
    }
    private val authApiService: FDUHoleAuthApiService by lazy {
        retrofit.create(FDUHoleAuthApiService::class.java)
    }


    override fun getScopeId(): String = "fduhole.com"

    suspend fun login(email: String, password: String): OTJWTToken = authApiService.login(OTLoginInfo(password, email))
}