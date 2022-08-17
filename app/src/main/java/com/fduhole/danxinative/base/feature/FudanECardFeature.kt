package com.fduhole.danxinative.base.feature

import android.content.Context
import com.fduhole.danxinative.R
import com.fduhole.danxinative.SingleFragmentActivity
import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.repository.fdu.ECardRepository
import com.fduhole.danxinative.ui.fdu.ECardFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

enum class FudanECardStatus {
    IDLE, LOADING, LOADED, ERROR
}

class FudanECardFeature : Feature(), KoinComponent {
    private val applicationContext: Context by inject()
    private val repo: ECardRepository by inject()

    private var status = FudanECardStatus.IDLE
    private var loadingJob: Job? = null
    private var eCardDescription: String = ""
    override fun inProgress(): Boolean = status == FudanECardStatus.LOADING
    override fun getClickable(): Boolean = true
    override fun getTitle(): String = "校园卡余额"
    override fun getIconId(): Int = R.drawable.ic_baseline_payment_24
    override fun getSubTitle(): String = when (status) {
        FudanECardStatus.IDLE -> "轻触以查看"
        FudanECardStatus.LOADING -> "加载中"
        FudanECardStatus.LOADED -> eCardDescription
        FudanECardStatus.ERROR -> "发生错误，轻触重试"
    }

    override fun onClick() {
        loadingJob?.cancel()
        when (status) {
            FudanECardStatus.LOADING -> {}
            FudanECardStatus.LOADED -> SingleFragmentActivity.showFragment(applicationContext, ECardFragment::class.java)
            else -> {
                status = FudanECardStatus.LOADING
                notifyChanged()
                loadingJob = featureScope.launch {
                    status = try {
                        val cardInfo = repo.getCardPersonInfo()
                        eCardDescription = cardInfo.balance
                        FudanECardStatus.LOADED
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        FudanECardStatus.ERROR
                    }
                    notifyChanged()
                }
            }
        }
    }
}