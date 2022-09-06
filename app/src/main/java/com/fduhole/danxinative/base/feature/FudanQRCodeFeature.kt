package com.fduhole.danxinative.base.feature

import android.content.Context
import android.os.Bundle
import com.fduhole.danxinative.R
import com.fduhole.danxinative.SingleFragmentActivity
import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.repository.fdu.ECardRepository
import com.fduhole.danxinative.ui.fdu.QRCodeFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

enum class FudanQRCodeStatus {
    IDLE, LOADING, ERROR
}

class FudanQRCodeFeature : Feature(), KoinComponent {
    private val appContext: Context by inject()
    private val repo: ECardRepository by inject()

    private var status = FudanQRCodeStatus.IDLE
    private var loadingJob: Job? = null
    private var errorDescription: String? = ""
    override fun inProgress(): Boolean = status == FudanQRCodeStatus.LOADING
    override fun getClickable(): Boolean = true
    override fun getTitle(): String = "复旦生活码"
    override fun getIconId(): Int = R.drawable.ic_baseline_qr_code_24
    override fun getSubTitle(): String = when (status) {
        FudanQRCodeStatus.IDLE -> "轻触以显示"
        FudanQRCodeStatus.LOADING -> "加载中"
        FudanQRCodeStatus.ERROR -> "发生错误，轻触重试：${errorDescription}"
    }

    override fun onClick() {
        loadingJob?.cancel()
        status = FudanQRCodeStatus.LOADING
        notifyChanged()
        loadingJob = featureScope.launch {
            status = try {
                SingleFragmentActivity.showFragment(appContext, QRCodeFragment::class.java, Bundle().apply {
                    putString(QRCodeFragment.ARG_QR_CODE, repo.getQRCode())
                })
                FudanQRCodeStatus.IDLE
            } catch (e: Throwable) {
                errorDescription = e.localizedMessage
                FudanQRCodeStatus.ERROR
            }
            notifyChanged()
        }
    }
}