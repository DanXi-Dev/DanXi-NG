package com.fduhole.danxinative.repository.fdu

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Request
import kotlin.coroutines.resume

class ZLAppRepository : BaseFDURepository() {
    companion object {
        const val GET_INFO_URL = "https://zlapp.fudan.edu.cn/ncov/wap/fudan/get-info"
    }

    override fun getUISLoginURL(): String =
        "https://uis.fudan.edu.cn/authserver/login?service=https%3A%2F%2Fzlapp.fudan.edu.cn%2Fa_fudanzlapp%2Fapi%2Fsso%2Findex%3Fredirect%3Dhttps%253A%252F%252Fzlapp.fudan.edu.cn%252Fsite%252Fncov%252FfudanDaily%253Ffrom%253Dhistory%26from%3Dwap"

    override fun getHost(): String = "zlapp.fudan.edu.cn"

    suspend fun getHistoryInfo(): String? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine {
            val res = client.newCall(Request.Builder().url(GET_INFO_URL).get().build()).execute()
            it.resume(res.body?.string())
        }
    }
}