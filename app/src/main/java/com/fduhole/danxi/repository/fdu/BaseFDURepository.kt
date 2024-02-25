package com.fduhole.danxi.repository.fdu

import androidx.annotation.StringRes
import com.fduhole.danxi.R
import com.fduhole.danxi.repository.BaseRepository
import com.fduhole.danxi.repository.settings.SettingsRepository
import com.fduhole.danxi.util.net.MemoryCookiesStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.Jsoup

abstract class BaseFDURepository(
    val settingsRepository: SettingsRepository,
) : BaseRepository() {

    abstract fun getUISLoginUrl(): Url

    open val maxRetryTimes = 2

    companion object {
        val uisLoginMutex = Mutex()
        const val UIS_HOST = "uis.fudan.edu.cn"

        sealed class UISLoginException(@StringRes val id: Int, message: String) : Throwable(message) {
            data object WeakPassword : UISLoginException(R.string.weak_password_note, "week_password")
            data object UnderMaintenance : UISLoginException(R.string.under_maintenance_note, "under_maintenance")
            data object Unknown : UISLoginException(R.string.uis_login_unknown_note, "unknown")
            data object NeedCaptcha : UISLoginException(R.string.need_captcha_note, "need_captcha")
            data object InvalidCredentials : UISLoginException(R.string.invalid_credentials_note, "invalid_credentials")
        }

        private val uisMessageExceptionMap = mapOf(
            "请输入验证码" to UISLoginException.NeedCaptcha,
            "密码有误" to UISLoginException.InvalidCredentials,
            "弱密码提示" to UISLoginException.WeakPassword,
            "网络维护中 | Under Maintenance" to UISLoginException.UnderMaintenance,
        )

        suspend fun login(
            id: String,
            password: String,
            loginUrl: Url,
        ): CookiesStorage {
            // 创建临时的 cookiesStorage，用于登录
            val tmpCookiesStorage = MemoryCookiesStorage()
            // 创建临时的 HttpClient，用于登录
            val tmpClient = createTmpClient(tmpCookiesStorage)

            // 获取登录页面
            val res = tmpClient.get(loginUrl)
            val doc = Jsoup.parse(res.body<String>())

            // 登录
            var response = tmpClient.submitForm(
                loginUrl.toString(),
                formParameters = parameters {
                    for (element in doc.select("input")) {
                        if (element.attr("type") != "button" && element.attr("name") != "username" && element.attr("name") != "password") {
                            append(element.attr("name"), element.attr("value"))
                        }
                    }
                    append("username", id)
                    append("password", password)
                }
            )

            // force redirect, see https://github.com/ktorio/ktor/issues/1623
            while (response.headers.contains("Location")) {
                val location = response.headers["Location"]!!
                response = tmpClient.get(location)
            }

            val body = response.body<String>()
            uisMessageExceptionMap.forEach { (message, exception) ->
                if (body.contains(message)) {
                    throw exception
                }
            }
            return tmpCookiesStorage
        }
    }

    suspend inline fun HttpClient.getUIS(
        url: Url,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse = getUIS {
        url(url)
        block()
    }

    suspend inline fun HttpClient.getUIS(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse = getUIS {
        url(urlString)
        block()
    }

    suspend inline fun HttpClient.getUIS(block: HttpRequestBuilder.() -> Unit) =
        getUIS(HttpRequestBuilder().apply(block))

    suspend inline fun HttpClient.getUIS(builder: HttpRequestBuilder): HttpResponse {
        builder.method = HttpMethod.Get
        return requestUIS(builder)
    }

    suspend inline fun HttpClient.submitFormUIS(
        url: Url,
        formParameters: Parameters = Parameters.Empty,
        encodeInQuery: Boolean = false,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = submitFormUIS(formParameters, encodeInQuery) {
        url(url)
        block()
    }

    suspend inline fun HttpClient.submitFormUIS(
        formParameters: Parameters = Parameters.Empty,
        encodeInQuery: Boolean = false,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = requestUIS {
        if (encodeInQuery) {
            method = HttpMethod.Get
            url.parameters.appendAll(formParameters)
        } else {
            method = HttpMethod.Post
            setBody(FormDataContent(formParameters))
        }

        block()
    }

    suspend inline fun HttpClient.requestUIS(
        block: HttpRequestBuilder.() -> Unit = {},
    ) = requestUIS(HttpRequestBuilder().apply(block))

    suspend inline fun HttpClient.requestUIS(builder: HttpRequestBuilder): HttpResponse {
        var response = request(builder) // process request with redirect
        repeat(maxRetryTimes) { _ ->
            if (builder.url.host.contains(UIS_HOST))
                return response
            if (response.request.url.host.contains(UIS_HOST)) {
                uisLoginMutex.withLock {
                    val uisInfo = settingsRepository.uisInfo.get() ?: throw UISLoginException.Unknown
                    cookiesStorage.replaceBy(
                        login(
                            id = uisInfo.id,
                            password = uisInfo.password,
                            loginUrl = getUISLoginUrl(),
                        )
                    )
                }
                response = request(builder)
            } else {
                return response
            }
        }
        throw UISLoginException.Unknown
    }
}
