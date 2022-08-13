package com.fduhole.danxinative.base.feature

import android.content.Context
import com.fduhole.danxinative.R
import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.repository.fdu.LibraryRepository
import com.fduhole.danxinative.state.GlobalState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

enum class FudanLibraryAttendanceStatus {
    IDLE, LOADING, LOADED, ERROR
}

class FudanLibraryAttendanceFeature : Feature(), KoinComponent {
    companion object {
        val LIBRARY_NAME = arrayOf(
            "文科馆", "理科馆", "医科馆1-6层",
            "张江馆", "江湾馆", "医科馆B1"
        )
    }

    private val repo: LibraryRepository by inject()

    private var status = FudanLibraryAttendanceStatus.IDLE
    private var loadingJob: Job? = null
    private var attendanceContent: String = ""

    override fun getClickable(): Boolean = true
    override fun getTitle(): String = "图书馆人数"
    override fun getIconId(): Int = R.drawable.ic_baseline_person_24
    override fun getSubTitle(): String = when (status) {
        FudanLibraryAttendanceStatus.IDLE -> "轻触以查看"
        FudanLibraryAttendanceStatus.LOADING -> "加载中"
        FudanLibraryAttendanceStatus.LOADED -> attendanceContent
        FudanLibraryAttendanceStatus.ERROR -> "发生错误，轻触重试"
    }

    override fun onClick() {
        loadingJob?.cancel()
        status = FudanLibraryAttendanceStatus.LOADING
        notifyRefresh()
        featureScope.launch {
            try {
                val attendanceList = repo.getAttendanceList()
                attendanceContent = LIBRARY_NAME.zip(attendanceList)
                    .map { "${it.first}: ${it.second} " }
                    .reduce { acc, s -> acc + s }
                status = FudanLibraryAttendanceStatus.LOADED
            } catch (e: Throwable) {
                status = FudanLibraryAttendanceStatus.ERROR
            }
            notifyRefresh()
        }
    }
}