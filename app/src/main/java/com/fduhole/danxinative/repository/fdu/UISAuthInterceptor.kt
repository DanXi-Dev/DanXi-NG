package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.state.GlobalState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.jsoup.Jsoup
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.CookieManager
import java.net.CookiePolicy

class UISAuthInterceptor(private val repository: BaseFDURepository) : Interceptor, KoinComponent {
    private val globalState: GlobalState by inject()

    companion object {
        const val UIS_HOST = "uis.fudan.edu.cn"
    }

    private fun login(): JavaNetCookieJar {
        val tmpCookieJar = JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        val tmpClient = OkHttpClient.Builder().cookieJar(tmpCookieJar).build()
        val res = tmpClient.newCall(Request.Builder().url(repository.getUISLoginURL()).get().build()).execute()
        val doc = Jsoup.parse(res.body!!.string())
        val payload = FormBody.Builder()
        for (element in doc.select("input")) {
            if (element.attr("type") != "button" && element.attr("name") != "username" && element.attr("name") != "password") {
                payload.add(element.attr("name"), element.attr("value"))
            }
        }
        payload.add("username", globalState.person?.id ?: "")
        payload.add("password", globalState.person?.password ?: "")

        tmpClient.newCall(Request.Builder().url(repository.getUISLoginURL()).post(payload.build()).build()).execute()
        return tmpCookieJar
    }

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val request = chain.request()
        println("Request from ${request.url}")
        var response = chain.proceed(request)
        if (request.url.host.contains(UIS_HOST)) return@runBlocking response

        if (response.request.url.host.contains(UIS_HOST)) {
            repository.cookieJar = login()
            response = repository.client.newCall(request).execute()
        }
        return@runBlocking response
    }
}