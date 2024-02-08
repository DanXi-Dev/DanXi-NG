package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.model.fdu.EhallStudentInfo
import com.fduhole.danxinative.repository.settings.SettingsRepository
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EhallRepository @Inject constructor(
    settingsRepository: SettingsRepository
) : BaseFDURepository(settingsRepository) {
    companion object {
        private val INFO_URL =
            Url("https://ehall.fudan.edu.cn/jsonp/ywtb/info/getUserInfoAndSchoolInfo.json")
        private val LOGIN_URL =
            Url("https://uis.fudan.edu.cn/authserver/login?service=http%3A%2F%2Fehall.fudan.edu.cn%2Flogin%3Fservice%3Dhttp%3A%2F%2Fehall.fudan.edu.cn%2Fywtb-portal%2Ffudan%2Findex.html")
    }

    override fun getUISLoginUrl() = LOGIN_URL

    override val scopeId = "ehall.fudan.edu.cn"

    suspend fun getStudentInfo(id: String, password: String): EhallStudentInfo = withContext(Dispatchers.IO) {
        // Execute manual logging in.
        // Login and absorb the auth cookie.
        val cookiesStorage = login(id, password, LOGIN_URL)
        val tmpClient = createTmpClient(cookiesStorage)
        val body: String = tmpClient.get(INFO_URL).body()
        val obj = JSONObject(body).getJSONObject("data")
        EhallStudentInfo(obj.optString("userName"), obj.optString("userTypeName"), obj.optString("userDepartment"))
    }
}