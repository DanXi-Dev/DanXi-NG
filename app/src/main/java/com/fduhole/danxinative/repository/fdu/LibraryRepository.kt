package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.repository.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.Response
import kotlin.coroutines.resume

class LibraryRepository : BaseRepository() {
    companion object {
        const val GET_INFO_URL = "http://10.55.101.62/book/show"
    }

    override fun getHost(): String = "10.55.101.62"

    /**
     * @return a list whose size is 6.
     *         The sequence is 文科馆、理科馆、医科馆1-6层、张江馆、江湾馆、医科馆B1
     */
    suspend fun getAttendanceList(): List<Int> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine {
                val res: Response = client.newCall(
                    Request.Builder()
                        .url(GET_INFO_URL).get()
                        .build()
                ).execute()
                val regex = "(?<=当前在馆人数：)[0-9]+".toRegex()
                val attendanceList = regex.findAll(res.body!!.string())
                    .map { elem -> elem.value.toIntOrNull()?: 0 }
                    .toList()
                it.resume(attendanceList)
            }
        }
}