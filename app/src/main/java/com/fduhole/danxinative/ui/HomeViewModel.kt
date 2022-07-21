package com.fduhole.danxinative.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxinative.base.Feature
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun initModel(callback: () -> Unit) {
        viewModelScope.launch {
            val demoFeature = object : Feature() {
                var subtitle: String = "轻触以查看"
                var progress: Boolean = false
                var loadingJob: Job? = null
                override fun inProgress(): Boolean = progress

                override fun getClickable(): Boolean = true
                override fun onClick() {
                    loadingJob?.cancel()
                    progress = true
                    notifyRefresh()
                    featureScope.launch {
                        delay(3000);
                        subtitle = "今日已打卡"
                        progress = false
                        notifyRefresh()
                    }
                }

                override fun getTitle(): String = "平安复旦"

                override fun getSubTitle(): String = subtitle

            }
            demoFeature.initFeature(callback, viewModelScope)
            _uiState.emit(HomeUiState(listOf(demoFeature)))
        }
    }
}