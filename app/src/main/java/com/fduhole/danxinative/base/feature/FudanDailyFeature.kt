package com.fduhole.danxinative.base.feature

import android.content.Context
import android.content.Intent
import com.fduhole.danxinative.BrowserActivity
import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.repository.fdu.ZLAppRepository
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.FDULoginUtils.Companion.uisLoginJavaScript
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

enum class FudanDailyStatus {
    NONE, ERROR, LOADING, NOT_TICKED, TICKED
}

class FudanDailyFeature : Feature(), KoinComponent {
    private val zlAppRepository: ZLAppRepository by inject()
    private val globalState: GlobalState by inject()
    private var status: FudanDailyStatus = FudanDailyStatus.NONE
    private var loadingJob: Job? = null
    private val applicationContext: Context by inject()

    override fun getTitle(): String = "平安复旦"

    override fun getSubTitle(): String = when (status) {
        FudanDailyStatus.NONE -> "轻触以查看"
        FudanDailyStatus.ERROR -> "发生错误，轻触重试"
        FudanDailyStatus.LOADING -> "加载中"
        FudanDailyStatus.NOT_TICKED -> "今日尚未打卡，轻触进行"
        FudanDailyStatus.TICKED -> "今日已打卡"
    }

    override fun inProgress(): Boolean = status == FudanDailyStatus.LOADING

    override fun getClickable(): Boolean = true

    override fun onClick() {
        loadingJob?.cancel()
        when (status) {
            FudanDailyStatus.NOT_TICKED -> {
                val intent = Intent(applicationContext, BrowserActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(BrowserActivity.KEY_URL, "https://zlapp.fudan.edu.cn/site/ncov/fudanDaily")
                    .putExtra(BrowserActivity.KEY_JAVASCRIPT, uisLoginJavaScript(globalState.person!!))
                    .putExtra(BrowserActivity.KEY_EXECUTE_IF_START_WITH, "https://uis.fudan.edu.cn/authserver/login")
                applicationContext.startActivity(intent)
            }
            FudanDailyStatus.LOADING -> {}
            FudanDailyStatus.TICKED -> {}
            else -> {
                status = FudanDailyStatus.LOADING
                notifyRefresh()

                loadingJob = featureScope.launch {
                    try {
                        status = if (zlAppRepository.hasTick()) {
                            FudanDailyStatus.TICKED
                        } else {
                            FudanDailyStatus.NOT_TICKED
                        }
                    } catch (e: Throwable) {
                        status = FudanDailyStatus.ERROR
                    }
                    notifyRefresh()
                }
            }
        }

    }


}