package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.repository.BaseRepository
import okhttp3.OkHttpClient

abstract class BaseFDURepository : BaseRepository() {
    override fun clientFactory(): OkHttpClient.Builder = super.clientFactory()
        .addInterceptor(UISAuthInterceptor(this))

    abstract fun getUISLoginURL(): String
}