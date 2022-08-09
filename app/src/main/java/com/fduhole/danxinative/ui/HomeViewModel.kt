package com.fduhole.danxinative.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxinative.base.feature.FudanAAONoticesFeature
import com.fduhole.danxinative.base.feature.FudanDailyFeature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private suspend fun buildFeatures() {
        _uiState.emit(HomeUiState(listOf(FudanDailyFeature(), FudanAAONoticesFeature())))
    }

    private suspend fun ensureFeatureBuilt() {
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