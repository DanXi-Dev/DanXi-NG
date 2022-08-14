package com.fduhole.danxinative.base.feature

import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.R
import com.fduhole.danxinative.model.DiningInfoItem
import com.fduhole.danxinative.repository.fdu.DatacenterRepository
import com.fduhole.danxinative.util.UnsuitableTimeException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class FudanDiningHallCrowdednessFeature : Feature(), KoinComponent {
    enum class Status {
        IDLE, LOADING, LOADED, UNSUITABLE, FAILED, ERROR
    }

    private val repo: DatacenterRepository by inject()

    private var subTitleContent = ""
    private var status: Status = Status.IDLE
    private var loadingJob: Job? = null
    private var trafficInfo: List<DiningInfoItem>? = null
    private var mostCrowded: DiningInfoItem? = null
    private var leastCrowded: DiningInfoItem? = null

    override fun getClickable(): Boolean = true
    override fun getIconId(): Int = R.drawable.ic_baseline_forum_24
    override fun getTitle(): String = "食堂排队消费状况"
    override fun getSubTitle(): String = when (status) {
        Status.IDLE -> "轻触以查看"
        Status.FAILED -> "加载失败"
        Status.ERROR -> "发生错误，轻触重试"
        Status.LOADING -> "加载中"
        Status.UNSUITABLE -> "现在不是用餐时间"
        Status.LOADED -> subTitleContent
    }

    override fun onClick() {
        loadingJob?.cancel()
        when (status) {
            Status.IDLE,
            Status.FAILED,
            Status.ERROR,
            Status.UNSUITABLE -> {
                loadingJob = featureScope.launch {
                    try {
                        trafficInfo = trafficInfo ?: repo.getCrowdednessInfo(0)
                        status = Status.LOADED
                    }
                    catch (e: UnsuitableTimeException) { status = Status.UNSUITABLE }
                    catch (e: Throwable) { status = Status.ERROR }
                    notifyRefresh()
                }
            }
            else -> {}
        }
    }
}