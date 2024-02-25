package com.fduhole.danxi.repository.fdu

import com.fduhole.danxi.model.fdu.LibraryInfo
import com.fduhole.danxi.repository.BaseRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor() : BaseRepository() {
    companion object {
        private val INFO_URL = Url("https://mlibrary.fudan.edu.cn/api/common/h5/getspaceseat")
    }

    override val scopeId = "mlibrary.fudan.edu.cn"

    @Serializable
    private data class AttendanceResponse(
        val msg: String,
        val code: String,
        val data: List<LibraryInfo>,
    )

    @OptIn(ExperimentalSerializationApi::class)
    override fun createClient() = createTmpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    /**
     * @return a list whose size is 6.
     *         The sequence is 文科馆、医科馆B1、江湾馆、张江馆、医科馆1-6层、理科馆
     */
    suspend fun getAttendance(): List<LibraryInfo> = withContext(Dispatchers.IO) {
        client.post(INFO_URL).body<AttendanceResponse>().data
    }
}