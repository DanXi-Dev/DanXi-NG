package com.fduhole.danxinative.repository.opentreehole

import com.fduhole.danxinative.repository.BaseRepository
import okhttp3.OkHttpClient

class FDUHoleRepository : BaseRepository() {
    override fun getHost(): String = "fduhole.com"
    override fun clientFactory(): OkHttpClient.Builder {
        return super.clientFactory()
    }
}