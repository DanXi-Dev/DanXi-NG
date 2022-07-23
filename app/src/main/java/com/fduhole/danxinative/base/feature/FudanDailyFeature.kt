package com.fduhole.danxinative.base.feature

import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.repository.fdu.ZLAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.component.KoinComponent

class FudanDailyFeature : Feature(), KoinComponent {
    private val zlAppRepository: ZLAppRepository by inject()
    private var subtitle: String = "轻触以查看"
    private var progress: Boolean = false
    private var loadingJob: Job? = null

    override fun getTitle(): String = "平安复旦"

    override fun getSubTitle(): String = subtitle

    override fun inProgress(): Boolean = progress

    override fun getClickable(): Boolean = true

    override fun onClick() {
        loadingJob?.cancel()
        progress = true
        notifyRefresh()

        loadingJob = featureScope.launch {
            withContext(Dispatchers.IO)
            {
                println(zlAppRepository.getHistoryInfo())
            }
            subtitle = "今日已打卡"
            progress = false
            notifyRefresh()
        }
    }
}