package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.repository.BaseRepository
import com.fduhole.danxinative.util.net.MemoryCookieJar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.JavaNetCookieJar
import okhttp3.Request
import org.json.JSONObject
import java.net.CookieManager
import java.net.CookiePolicy
import kotlin.coroutines.resume

data class StudentInfo(val name: String?, val userTypeName: String?, val userDepartment: String?)
class EhallRepository : BaseRepository() {
    companion object {
        const val INFO_URL =
            "https://ehall.fudan.edu.cn/jsonp/ywtb/info/getUserInfoAndSchoolInfo.json"
        const val LOGIN_URL =
            "https://uis.fudan.edu.cn/authserver/login?service=http%3A%2F%2Fehall.fudan.edu.cn%2Flogin%3Fservice%3Dhttp%3A%2F%2Fehall.fudan.edu.cn%2Fywtb-portal%2Ffudan%2Findex.html"
    }


    override fun getHost(): String = "ehall.fudan.edu.cn"

    suspend fun getStudentInfo(info: PersonInfo): StudentInfo = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine {
            // Execute manual logging in.
            val tmpMemoryCookieJar = MemoryCookieJar(JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL)))
            val tmpClient = clientFactoryNoCookie().cookieJar(tmpMemoryCookieJar).build()
            // Login and absorb the auth cookie.
            tmpMemoryCookieJar.replaceBy(UISAuthInterceptor.login(info.id, info.password, LOGIN_URL))

            val response = tmpClient.newCall(Request.Builder().url(INFO_URL).get().build()).execute()
            val obj = JSONObject(response.body?.string() ?: "").getJSONObject("data")
            it.resume(StudentInfo(obj.optString("userName"), obj.optString("userTypeName"), obj.optString("userDepartment")))
        }
    }
}