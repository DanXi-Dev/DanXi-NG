package com.fduhole.danxinative.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxinative.base.Feature
import com.fduhole.danxinative.repository.fdu.ZLAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val zlAppRepository: ZLAppRepository by inject()

    private suspend fun buildFeatures() {
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

            override fun getTitle(): String = "平安复旦"

            override fun getSubTitle(): String = subtitle

        }
        _uiState.emit(HomeUiState(listOf(demoFeature)))
    }

    suspend fun ensureFeatureBuilt() {
        if (_uiState.value.features.isEmpty()) {
            buildFeatures()
        }
    }

    fun initModel(featureCallback: () -> Unit) {
        viewModelScope.launch {
            ensureFeatureBuilt()
            for (feature in _uiState.value.features) {
                feature.initFeature(featureCallback, viewModelScope)
            }
        }
    }
}