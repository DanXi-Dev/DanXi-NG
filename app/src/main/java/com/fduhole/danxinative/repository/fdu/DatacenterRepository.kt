package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.model.DiningInfoItem
import com.fduhole.danxinative.util.DataUtils
import com.fduhole.danxinative.util.UnsuitableTimeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Request
import kotlin.coroutines.resume

class DatacenterRepository : BaseFDURepository() {
    companion object {
        const val DINING_DETAIL_URL = "https://my.fudan.edu.cn/simple_list/stqk"
        const val DINING_HALL_CLOSING_NOTICE = "本功能仅在用餐时段开放"
        const val DATA = "initChart('chart_bb', ['光华楼\\n光华咖啡','光华楼-光华咖啡(学府餐饮)','光华楼-光华咖啡(学校餐饮)','北区食堂-北区二楼德保','北区\\n千喜鹤','北区\\n新世纪早餐','北区食堂-北区新世纪早餐(高校)','北区\\n清真','北区食堂-北区清真(伊源)','北区\\n西餐厅','北区食堂-北区西餐厅(乐烹西东)','北区\\n面包房','北区食堂-北区面包房(东兴鼎昊)','北区\\n颐谷','南区\\n一楼同茂兴','南区\\n中快餐饮','南区\\n清真','南区食堂-南区清真(伊源)','南区\\n南苑餐厅','南区食堂-南苑餐厅(东大)','南区\\n同茂兴','南区\\n教工快餐','南区食堂-教工快餐(东大)','文图咖啡馆','旦苑\\n清真','旦苑\\n一楼大厅','旦苑\\n教授餐厅','旦苑\\n二楼大厅','旦苑\\n面包房','旦苑-本部学校面包房(学校餐饮)','旦苑-本部西餐厅(乐烹西东)','旦苑\\n西餐厅'],\n['0','0','0','0','0','0','9','0','0','0','2','0','3','1','3','0','0','0','0','0','0','0','0','0','0','7','0','0','0','2','0','0'],\n['5','2','2','77','118','79','66','45','36','54','30','52','21','134','167','51','49','36','100','74','113','171','109','10','79','232','44','167','75','57','35','46'])\ninitChart('chart_fl', ['书院楼西园餐厅','书院楼西园餐厅(养吉)','书院楼风味餐厅','书院楼风味餐厅(颐谷)','护理学院','枫林清真餐厅-枫林清真餐厅','枫林清真餐厅-枫林清真餐厅(伊源)','枫林食堂-枫林一楼科桥'],\n['0','0','0','5','0','0','0','1'],\n['49','26','167','107','37','60','49','144'])\ninitChart('chart_jw', ['一楼中快','二楼颐谷','清真','清真(伊源)','点心','点心(中快)','花园餐厅','花园餐厅(雷汇柏祺)'],\n['13','0','0','1','0','1','0','0'],\n['209','133','46','40','39','29','10','1'])\ninitChart('chart_zj', ['一餐二楼教师','一餐二楼自选','一餐二楼风味','一楼中快','佳乐餐饮','清真','清真(伊源)'],\n['0','0','0','0','0','0','0'],\n['22','41','23','67','29','32','26'])\n"
    }

    suspend fun getCrowdednessInfo(areaCode: Int) = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine {
            val response = client.newCall(
                Request.Builder()
                    .url(DINING_DETAIL_URL).get()
                    .build()
            ).execute()
            val data = response.body!!.string()
            if (data.contains(DINING_HALL_CLOSING_NOTICE)) {
                throw UnsuitableTimeException()
            }

            val begin = data.indexOf("initChart('")
            val end = data.lastIndexOf("</script>")

            val chartData = data.substring(begin, end).replace("'", "\"")
            // val chartData = DATA.replace("'", "\"")

            val jsonExtraction = "\\[.+\\]".toRegex().findAll(chartData)
            val names = Json.decodeFromString<List<String>>(
                jsonExtraction.elementAt(areaCode * 3).groups[0]!!.value)
            val currentData = Json.decodeFromString<List<Int>>(
                jsonExtraction.elementAt(areaCode * 3 + 1).groups[0]!!.value)
            val highestData = Json.decodeFromString<List<Int>>(
                jsonExtraction.elementAt(areaCode * 3 + 2).groups[0]!!.value)

            val datas = DataUtils.zipToTriple(names, currentData, highestData)
            it.resume(datas.map { it -> DiningInfoItem(it.first, it.second, it.third) })
        }
    }

    override fun getHost(): String = "my.fudan.edu.cn"

    override fun getUISLoginURL(): String =
        "https://uis.fudan.edu.cn/authserver/login?service=https%3A%2F%2Fmy.fudan.edu.cn%2Fsimple_list%2Fstqk";
}