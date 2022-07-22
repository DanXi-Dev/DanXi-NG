package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.repository.BaseRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

abstract class BaseFDURepository : BaseRepository() {
    companion object {
        val loginJobs: MutableMap<String, Job?> = mutableMapOf()
    }

    var loginJob: Job?
        get() = loginJobs[getUISLoginURL()]
        set(value) {
            loginJobs[getUISLoginURL()] = value
        }

    override fun clientFactory(): OkHttpClient.Builder {
        return super.clientFactory()
            .addInterceptor(UISAuthInterceptor(this))
    }

    abstract fun getUISLoginURL(): String
}