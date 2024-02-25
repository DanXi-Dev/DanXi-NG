package com.fduhole.danxi.repository.fdu

import com.fduhole.danxi.model.fdu.CardPersonInfo
import com.fduhole.danxi.model.fdu.CardRecord
import com.fduhole.danxi.repository.settings.SettingsRepository
import com.fduhole.danxi.util.appendAll
import com.fduhole.danxi.util.between
import com.fduhole.danxi.util.toDateTimeString
import io.ktor.client.call.body
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.headers
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class ECardRepository @Inject constructor(
    settingsRepository: SettingsRepository
) : BaseFDURepository(settingsRepository) {
    override fun getUISLoginUrl() = LOGIN_URL
    override val scopeId: String = "fudan.edu.cn"

    companion object {
        private val LOGIN_URL =
            Url("https://uis.fudan.edu.cn/authserver/login?service=https%3A%2F%2Fecard.fudan.edu.cn%2Fepay%2Fj_spring_cas_security_check")
        val USER_DETAIL_URL = Url("https://ecard.fudan.edu.cn/epay/myepay/index")
        val CONSUME_DETAIL_URL = Url("https://ecard.fudan.edu.cn/epay/consume/query")
        val CONSUME_DETAIL_CSRF_URL = Url("https://ecard.fudan.edu.cn/epay/consume/index")
        private val CONSUME_DETAIL_HEADER = mapOf(
            HttpHeaders.UserAgent to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0",
            HttpHeaders.Accept to "text/xml",
            HttpHeaders.AcceptLanguage to "zh-CN,en-US;q=0.7,en;q=0.3",
            HttpHeaders.ContentType to "application/x-www-form-urlencoded",
            HttpHeaders.Origin to "https://ecard.fudan.edu.cn",
            "DNT" to "1",
            HttpHeaders.Connection to "keep-alive",
            HttpHeaders.Referrer to "https://ecard.fudan.edu.cn/epay/consume/index",
            "Sec-GPC" to "1"
        )
        val QR_URL = Url("https://ecard.fudan.edu.cn/epay/wxpage/fudan/zfm/qrcode")
    }

    /**
     * Get personal info (and card info).
     */
    suspend fun getCardPersonInfo(): CardPersonInfo = withContext(Dispatchers.IO) {
        val body: String = client.getUIS(USER_DETAIL_URL).body()
        val soup = Jsoup.parse(body)
        val balance = requireNotNull(soup.selectFirst(".payway-box-bottom-item > p")?.text()) { "balance cannot be null." }
        val name = requireNotNull(soup.selectFirst(".custname")?.text()?.between("您好，", "！")) { "name cannot be null." }
        val recentRecord = soup.select(".tab-content > #all tbody tr").map {
            val details = requireNotNull(it.getElementsByTag("td")) { "td element cannot be null." }
            val dateStr = details[0].child(0).text().replace(".", "-")
            val timeStr = details[0].child(1).text().trim()
            CardRecord(
                time = LocalDateTime.parse("${dateStr}T${timeStr}").toInstant(TimeZone.currentSystemDefault()),
                type = details[1].child(0).text().trim(),
                location = details[2].text().trim().replace("&nbsp;", "").trim(),
                amount = details[3].text().trim().replace("&nbsp;", "").trim(),
                balance = details[4].text().trim().replace("&nbsp;", "").trim(),
            )
        }
        CardPersonInfo(balance, name, recentRecord)
    }

    /**
     * Get the card records of last [dayNum] days.
     *
     * If [dayNum] = 0, it returns the last ten records;
     * if [dayNum] < 0, it throws an error.
     */
    suspend fun getCardRecords(dayNum: Int): List<CardRecord> = withContext(Dispatchers.IO) {
        val payloadAndPageNum = getPagedCardRecordsPayloadAndPageNum(dayNum)
        val list = arrayListOf<CardRecord>()
        for (i in 1..payloadAndPageNum.second) {
            list.addAll(getPagedCardRecords(payloadAndPageNum.first, i))
        }
        list
    }

    suspend fun getPagedCardRecords(payload: Map<String, String>, pageIndex: Int): List<CardRecord> = withContext(Dispatchers.IO) {
        requireNotNull(loadOnePageCardRecord(payload, pageIndex)) { "Got null data at page index $pageIndex" }
    }

    suspend fun getPagedCardRecordsPayloadAndPageNum(dayNum: Int): Pair<Map<String, String>, Int> {
        require(dayNum >= 0) { "Day number should not be less than 0." }
        val consumeCsrfPage: String = client.getUIS(CONSUME_DETAIL_CSRF_URL).body()
        val consumeCsrfPageSoup = Jsoup.parse(consumeCsrfPage)
        val metas = consumeCsrfPageSoup.getElementsByTag("meta")
        val element = metas.find { e -> e.attr("name") == "_csrf" }
        val csrfId = requireNotNull(element?.attr("content")) { "CSRF id is null." }

        // Build the request body.
        val end = Clock.System.now()
        val backDays = if (dayNum == 0) 180 else dayNum
        val start = end.minus(backDays.days)
        val datePattern = "yyyy-MM-dd"
        val payload = mapOf(
            "aaxmlrequest" to "true",
            "pageNo" to "1",
            "tabNo" to "1",
            "pager.offset" to "10",
            "tradename" to "",
            "starttime" to start.toDateTimeString(datePattern),
            "endtime" to end.toDateTimeString(datePattern),
            "timetype" to "1",
            "_tradedirect" to "on",
            "_csrf" to csrfId,
        )
        // Get the number of pages, only when logDays > 0.
        var totalPages = 1
        if (dayNum > 0) {
            val detailBody: String = client.submitFormUIS(
                CONSUME_DETAIL_URL,
                formParameters = parameters {
                    appendAll(payload)
                },
            ).body()
            totalPages = detailBody.between("</b>/", "页")?.toIntOrNull() ?: 0
        }
        return payload to totalPages
    }

    suspend fun getQRCode(): String = withContext(Dispatchers.IO) {
        val body: String = client.getUIS(QR_URL).body()
        val soup = Jsoup.parse(body)
        val result = soup.selectFirst("#myText")?.attr("value")
        requireNotNull(result) { "Cannot find `value` in the response page." }
    }

    private suspend fun loadOnePageCardRecord(payload: Map<String, String>, pageIndex: Int): List<CardRecord>? = withContext(Dispatchers.IO) {
        val requestData = payload.toMutableMap()
        requestData["pageNo"] = pageIndex.toString()
        val detailBody: String = client.submitFormUIS(
            CONSUME_DETAIL_URL,
            formParameters = parameters {
                appendAll(requestData)
            },
        ) {
            headers {
                CONSUME_DETAIL_HEADER.forEach { (k, v) ->
                    append(k, v)
                }
            }
        }.body()
        val soup = detailBody.between("<![CDATA[", "]]>")?.let { Jsoup.parse(it) }
        val elements = soup?.selectFirst("tbody")?.getElementsByTag("tr")
        elements?.map {
            val details = requireNotNull(it.getElementsByTag("td")) { "td element cannot be null." }
            val dateStr = details[0].child(0).text().replace(".", "-")
            val timeStr = details[0].child(1).text().trim()
            CardRecord(
                time = LocalDateTime.parse("${dateStr}T${timeStr}").toInstant(TimeZone.currentSystemDefault()),
                type = details[1].child(0).text().trim(),
                location = details[2].text().trim().replace("&nbsp;", ""),
                amount = details[3].text().trim().replace("&nbsp;", ""),
                balance = details[4].text().trim().replace("&nbsp;", "").trim(),
            )
        }
    }
}