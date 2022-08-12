package com.fduhole.danxinative.base.feature

import android.content.Context
import com.fduhole.danxinative.R
import com.fduhole.danxinative.SingleFragmentActivity
import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.ui.fdu.AAONoticesFragment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FudanAAONoticesFeature : Feature(), KoinComponent {
    private val applicationContext: Context by inject()
    override fun getClickable(): Boolean = true

    override fun getTitle(): String = "教务处通知"
    override fun getIconId(): Int = R.drawable.ic_baseline_newspaper_24

    override fun getSubTitle(): String = "轻触以查看"
    override fun onClick() = SingleFragmentActivity.showFragment(applicationContext, AAONoticesFragment::class.java)
}