package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.repository.BaseRepository
import okhttp3.CookieJar
import okhttp3.OkHttpClient

abstract class BaseFDURepository: BaseRepository() {
    companion object {
        val loginJobs: MutableMap<String, CookieJar> = mutableMapOf()
    }
}