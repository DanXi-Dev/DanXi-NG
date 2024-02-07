package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.model.fdu.AAONotice
import com.fduhole.danxinative.state.GlobalState
import io.ktor.client.call.body
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup
import javax.inject.Inject

class AAORepository @Inject constructor(
    globalState: GlobalState,
) : BaseFDURepository(globalState) {
    companion object {
        const val TYPE_NOTICE_ANNOUNCEMENT = "9397"
        private val LOGIN_URL =
            Url("https://uis.fudan.edu.cn/authserver/login?service=http%3A%2F%2Fjwc.fudan.edu.cn%2Feb%2Fb7%2Fc9397a388023%2Fpage.psp")
        private const val HOST = "https://jwc.fudan.edu.cn"
    }

    override fun getUISLoginUrl() = LOGIN_URL

    override val scopeId = "fudan.edu.cn"

    private fun getNoticeListUrl(type: String, page: Int): String = "$HOST/$type/list${if (page <= 1) "" else page}.htm"

    suspend fun getNoticeList(page: Int, type: String = TYPE_NOTICE_ANNOUNCEMENT): List<AAONotice> = with(Dispatchers.IO) {
        val res: String = client.getUIS(getNoticeListUrl(type, page)).body()
        val doc = Jsoup.parse(res)
        doc.select(".wp_article_list_table > tbody > tr > td > table > tbody").map {
            val noticeInfo = it.select("tr").select("td")
            AAONotice(
                title = noticeInfo[0].text().trim(),
                url = HOST + noticeInfo[0].select("a").attr("href"),
                time = noticeInfo[1].text().trim()
            )
        }
    }
}