package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.repository.BaseRepository
import com.fduhole.danxinative.state.GlobalState
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.jsoup.Jsoup
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.CookieManager
import java.net.CookiePolicy

class UISAuthInterceptor(private val repository: BaseFDURepository) : Interceptor, KoinComponent {
    private val globalState: GlobalState by inject()
    private var secondPerson: PersonInfo? = null

    /// If [secondPersonInfo] is set, the interceptor will not use user info provided by [globalState], but [secondPersonInfo].
    constructor(repository: BaseRepository, secondPersonInfo: PersonInfo, loginUrl: String) : this(object : BaseFDURepository() {
        override fun getUISLoginURL(): String = loginUrl
        override fun getHost(): String = repository.getHost()
    }) {
        secondPerson = secondPersonInfo
    }

    companion object {
        const val UIS_HOST = "uis.fudan.edu.cn"
        fun login(id: String, password: String, loginUrl: String): JavaNetCookieJar {
            val tmpCookieJar = JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL))
            val tmpClient = OkHttpClient.Builder().cookieJar(tmpCookieJar).build()
            val res = tmpClient.newCall(Request.Builder().url(loginUrl).get().build()).execute()
            val doc = Jsoup.parse(res.body!!.string())
            val payload = FormBody.Builder()
            for (element in doc.select("input")) {
                if (element.attr("type") != "button" && element.attr("name") != "username" && element.attr("name") != "password") {
                    payload.add(element.attr("name"), element.attr("value"))
                }
            }
            payload.add("username", id)
            payload.add("password", password)

            tmpClient.newCall(Request.Builder().url(loginUrl).post(payload.build()).build()).execute()
            return tmpCookieJar
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val request = chain.request()
        var response = chain.proceed(request)
        if (request.url.host.contains(UIS_HOST)) return@runBlocking response

        if (response.request.url.host.contains(UIS_HOST)) {
            repository.cookieJar.replace(login(globalState.person?.id ?: "", globalState.person?.password ?: "", repository.getUISLoginURL()))
            response = repository.client.newCall(request).execute()
        }
        return@runBlocking response
    }
}