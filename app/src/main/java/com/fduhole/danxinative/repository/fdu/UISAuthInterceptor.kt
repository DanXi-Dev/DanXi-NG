package com.fduhole.danxinative.repository.fdu

import android.content.res.Resources
import com.fduhole.danxinative.R
import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.ExplainableException
import com.fduhole.danxinative.util.net.RetryCount
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        UISLoginExceptionType.Unknown -> context.getString(R.string.uis_login_unknown_note)
    }
}

class UISLoginUnrecoverableException(val type: UISLoginUnrecoverableExceptionType) : ExplainableException() {
    override fun explain(context: Resources): String = when (type) {
        UISLoginUnrecoverableExceptionType.NeedCaptcha -> context.getString(R.string.need_captcha_note)
        UISLoginUnrecoverableExceptionType.InvalidCredentials -> context.getString(R.string.invalid_credentials_note)
    }
}

enum class UISLoginExceptionType {
    WeakPassword, UnderMaintenance, Unknown
}

enum class UISLoginUnrecoverableExceptionType {
    NeedCaptcha, InvalidCredentials
}

class UISAuthInterceptor(private val repository: BaseFDURepository, private val tempPersonInfo: PersonInfo? = null, private val maxRetryTimes: Int = 2) : Interceptor,
    KoinComponent {
    private val globalState: GlobalState by inject()
    private val personInfo: PersonInfo? by lazy { tempPersonInfo ?: globalState.person }

    companion object {
        private val mutex = Mutex()
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
        val retryCount = request.tag(RetryCount::class.java)
        if (retryCount != null && retryCount.retryTime > maxRetryTimes) {
            throw UISLoginException(UISLoginExceptionType.Unknown)
        }
        var response = chain.proceed(request)
        if (request.url.host.contains(UIS_HOST)) return@runBlocking response

        if (response.request.url.host.contains(UIS_HOST)) {
            mutex.withLock {
                repository.cookieJar.replaceBy(login(personInfo?.id.orEmpty(), personInfo?.password.orEmpty(), repository.getUISLoginURL()))
                response = repository.client
                    .newCall(request.newBuilder()
                        .tag(RetryCount((retryCount?.retryTime ?: 0) + 1))
                        .build())
                    .execute()
            }
        }
        return@runBlocking response
    }
}