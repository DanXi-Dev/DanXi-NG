package com.fduhole.danxinative.repository.fdu

import android.content.res.Resources
import com.fduhole.danxinative.R
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.ExplainableException
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.jsoup.Jsoup
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.CookieManager
import java.net.CookiePolicy

class UISLoginException(val type: UISLoginExceptionType) : ExplainableException() {
    override fun explain(context: Resources): String = when (type) {
        UISLoginExceptionType.WeakPassword -> context.getString(R.string.weak_password_note)
        UISLoginExceptionType.UnderMaintenance -> context.getString(R.string.under_maintenance_note)
    }
}

class UISLoginUnrecoverableException(val type: UISLoginUnrecoverableExceptionType) : ExplainableException() {
    override fun explain(context: Resources): String = when (type) {
        UISLoginUnrecoverableExceptionType.NeedCaptcha -> context.getString(R.string.need_captcha_note)
        UISLoginUnrecoverableExceptionType.InvalidCredentials -> context.getString(R.string.invalid_credentials_note)
    }
}

enum class UISLoginExceptionType {
    WeakPassword, UnderMaintenance
}

enum class UISLoginUnrecoverableExceptionType {
    NeedCaptcha, InvalidCredentials
}

class UISAuthInterceptor(private val repository: BaseFDURepository) : Interceptor, KoinComponent {
    private val globalState: GlobalState by inject()

    companion object {
        const val UIS_HOST = "uis.fudan.edu.cn"
        const val CAPTCHA_CODE_NEEDED = "请输入验证码";
        const val CREDENTIALS_INVALID = "密码有误";
        const val WEAK_PASSWORD = "弱密码提示";
        const val UNDER_MAINTENANCE = "网络维护中 | Under Maintenance";
        fun login(id: String, password: String, loginUrl: String): JavaNetCookieJar {
            val tmpCookieJar = JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL))
            val tmpClient = OkHttpClient.Builder().cookieJar(tmpCookieJar).cache(null).build()
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

            val response = tmpClient.newCall(Request.Builder().url(loginUrl).post(payload.build()).build()).execute()
            val responseBody = response.body?.string().orEmpty()
            if (responseBody.contains(CAPTCHA_CODE_NEEDED)) {
                throw UISLoginUnrecoverableException(UISLoginUnrecoverableExceptionType.NeedCaptcha)
            } else if (responseBody.contains(CREDENTIALS_INVALID)) {
                throw UISLoginUnrecoverableException(UISLoginUnrecoverableExceptionType.InvalidCredentials)
            } else if (responseBody.contains(UNDER_MAINTENANCE)) {
                throw UISLoginException(UISLoginExceptionType.UnderMaintenance)
            } else if (responseBody.contains(WEAK_PASSWORD)) {
                throw UISLoginException(UISLoginExceptionType.WeakPassword)
            }
            return tmpCookieJar
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val request = chain.request()
        var response = chain.proceed(request)
        if (request.url.host.contains(UIS_HOST)) return@runBlocking response

        if (response.request.url.host.contains(UIS_HOST)) {
            repository.cookieJar.replaceBy(login(globalState.person?.id.orEmpty(), globalState.person?.password.orEmpty(), repository.getUISLoginURL()))
            response = repository.client.newCall(request).execute()
        }
        return@runBlocking response
    }
}