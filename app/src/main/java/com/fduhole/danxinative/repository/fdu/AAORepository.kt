package com.fduhole.danxinative.repository.fdu

import android.util.Log
import com.fduhole.danxinative.model.AAONotice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.wait
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class AAORepository : BaseFDURepository() {
    companion object {
        const val TYPE_NOTICE_ANNOUNCEMENT = "9397"
    }

    override fun getUISLoginURL(): String =
        "https://uis.fudan.edu.cn/authserver/login?service=http%3A%2F%2Fjwc.fudan.edu.cn%2Feb%2Fb7%2Fc9397a388023%2Fpage.psp";

    override fun getHost(): String = "https://jwc.fudan.edu.cn"

    fun getNoticeListUrl(type: String, page: Int): String =
        "${getHost()}/$type/list${if (page <= 1) "" else page}.htm"

    suspend fun getNoticeList(page: Int, type: String = TYPE_NOTICE_ANNOUNCEMENT): List<AAONotice>? =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine {
                val res: Response = client.newCall(
                    Request.Builder()
                        .url(getNoticeListUrl(type, page)).get()
                        .build()
                ).execute()
                val doc = Jsoup.parse(res.body!!.string())
                val noticeList = ArrayList<AAONotice>()
                for (element in doc.select(".wp_article_list_table > tbody > tr > td > table > tbody")) {
                    val noticeInfo: Elements = element.select("tr").select("td")
                    val notice = AAONotice(
                        noticeInfo[0].text().trim(),
                        getHost() + noticeInfo[0].select("a").attr("href"),
                        noticeInfo[1].text().trim()
                    )
                    noticeList.add(notice)
                }
                it.resume(noticeList)
            }
        }
}