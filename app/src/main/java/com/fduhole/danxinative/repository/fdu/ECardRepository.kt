package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.model.CardPersonInfo
import com.fduhole.danxinative.model.CardRecord
import com.fduhole.danxinative.util.addMap
import com.fduhole.danxinative.util.between
import com.fduhole.danxinative.util.toDateTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import okhttp3.FormBody
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import org.jsoup.Jsoup
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.days

class ECardRepository : BaseFDURepository() {
    override fun getUISLoginURL(): String = "https://uis.fudan.edu.cn/authserver/login?service=https%3A%2F%2Fecard.fudan.edu.cn%2Fepay%2Fj_spring_cas_security_check"

    override fun getScopeId(): String = "fudan.edu.cn"

    companion object {
        const val USER_DETAIL_URL = "https://ecard.fudan.edu.cn/epay/myepay/index"
        const val CONSUME_DETAIL_URL = "https://ecard.fudan.edu.cn/epay/consume/query"
        const val CONSUME_DETAIL_CSRF_URL = "https://ecard.fudan.edu.cn/epay/consume/index"
        val CONSUME_DETAIL_HEADER = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0",
            "Accept" to "text/xml",
            "Accept-Language" to "zh-CN,en-US;q=0.7,en;q=0.3",
            "Content-Type" to "application/x-www-form-urlencoded",
            "Origin" to "https://ecard.fudan.edu.cn",
            "DNT" to "1",
            "Connection" to "keep-alive",
            "Referer" to "https://ecard.fudan.edu.cn/epay/consume/index",
            "Sec-GPC" to "1"
        )
        const val QR_URL = "https://ecard.fudan.edu.cn/epay/wxpage/fudan/zfm/qrcode"
    }

    /**
     * Get personal info (and card info).
     */
    suspend fun getCardPersonInfo(): CardPersonInfo = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine {
            val res = client.newCall(Request.Builder().get().url(USER_DETAIL_URL).build()).execute()
            val body = res.body?.string()
            val balance = requireNotNull(body?.between("<p>账户余额：", "元</p>")) { "balance cannot be null." }
            val name = requireNotNull(body?.between("姓名：", "</p>")) { "name cannot be null." }
            it.resume(CardPersonInfo(balance, name))
        }
    }

    /**
     * Get the card records of last [dayNum] days.
     *
     * If [dayNum] = 0, it returns the last ten records;
     * if [dayNum] < 0, it throws an error.
     */
    suspend fun getCardRecords(dayNum: Int): List<CardRecord> {
        val payloadAndPageNum = getPagedCardRecordsPayloadAndPageNum(dayNum)
        val list = arrayListOf<CardRecord>()
        for (i in 1..payloadAndPageNum.second) {
            list.addAll(getPagedCardRecords(payloadAndPageNum.first, i))
        }
        return list
    }

    suspend fun getPagedCardRecords(payload: Map<String, String>, pageIndex: Int): List<CardRecord> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine {
            it.resume(requireNotNull(loadOnePageCardRecord(payload, pageIndex)) { "Got null data at page index $pageIndex" })
        }
    }

    suspend fun getPagedCardRecordsPayloadAndPageNum(dayNum: Int): Pair<Map<String, String>, Int> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine {
            require(dayNum >= 0) { "Day number should not be less than 0." }
            val consumeCsrfPageResponse = client.newCall(Request.Builder().get().url(CONSUME_DETAIL_CSRF_URL).build()).execute()
            val consumeCsrfPageSoup = consumeCsrfPageResponse.body?.string()?.let { it1 -> Jsoup.parse(it1) }
            val metas = consumeCsrfPageSoup?.getElementsByTag("meta")
            val element = metas?.find { e -> e.attr("name") == "_csrf" }
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
                val detailResponse = client.newCall(Request.Builder()
                    .post(FormBody.Builder().addMap(payload).build())
                    .url(CONSUME_DETAIL_URL)
                    .headers(CONSUME_DETAIL_HEADER.toHeaders())
                    .build()).execute()
                totalPages = detailResponse.body?.string()?.between("</b>/", "页")?.toIntOrNull() ?: 0
            }
            it.resume(payload to totalPages)
        }
    }

    suspend fun getQRCode(): String = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine {
            val response = client.newCall(Request.Builder()
                .get()
                .url(QR_URL)
                .build()).execute()
            val soup = Jsoup.parse(requireNotNull(response.body?.string()) { "Get null response from server." })
            val result = soup.selectFirst("#myText")?.attr("value")
            it.resume(requireNotNull(result) { "Cannot find `value` in the response page." })
        }
    }

    private fun loadOnePageCardRecord(payload: Map<String, String>, pageIndex: Int): List<CardRecord>? {
        val requestData = payload.toMutableMap()
        requestData["pageNo"] = pageIndex.toString()
        val detailResponse = client.newCall(Request.Builder()
            .post(FormBody.Builder().addMap(requestData).build())
            .url(CONSUME_DETAIL_URL)
            .headers(CONSUME_DETAIL_HEADER.toHeaders())
            .build()).execute()
        val soup = detailResponse.body?.string()?.between("<![CDATA[", "]]>")?.let { Jsoup.parse(it) }
        val elements = soup?.selectFirst("tbody")?.getElementsByTag("tr")
        return elements?.map {
            val details = requireNotNull(it.getElementsByTag("td")) { "td element cannot be null." }
            val dateStr = details[0].child(0).text().replace(".", "-")
            val timeStr = details[0].child(1).text().trim().chunked(2).joinToString(":")
            CardRecord(
                LocalDateTime.parse("${dateStr}T${timeStr}").toInstant(TimeZone.currentSystemDefault()),
                details[1].child(0).text().trim(),
                details[2].text().trim().replace("&nbsp;", ""),
                details[3].text().trim().replace("&nbsp;", ""),
            )
        }
    }
}