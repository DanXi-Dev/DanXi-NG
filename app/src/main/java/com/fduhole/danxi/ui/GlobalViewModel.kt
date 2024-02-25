package com.fduhole.danxi.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxi.model.opentreehole.OTJWTToken
import com.fduhole.danxi.repository.settings.SettingsRepository
import com.fduhole.danxi.ui.component.fdu.FudanStateHolder
import com.fduhole.danxi.util.LoginStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GlobalViewState(
    val isDarkTheme: Boolean? = null,
    val highContrastColor: Boolean = false,
)

data class FDUUISViewState(
    val id: String,
    val password: String,
    val name: String,
)

data class FDUHoleViewState(
    val token: OTJWTToken,
    val id: Int,
)

@HiltViewModel
class GlobalViewModel @Inject constructor(
    val settingsRepository: SettingsRepository,
    val fudanStateHolder: FudanStateHolder,
) : ViewModel() {
    private val _fduHoleState = MutableStateFlow<LoginStatus<out FDUHoleViewState>>(LoginStatus.NotLogin)

    val uiState = settingsRepository.run {
        combine(
            darkTheme.flow,
            highContrastColor.flow,
        ) { darkTheme, highContrastColor ->
            GlobalViewState(
                isDarkTheme = darkTheme,
                highContrastColor = highContrastColor ?: false,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, GlobalViewState())
    }
    val fduHoleState = _fduHoleState.asStateFlow()

    var settingsExpanded by mutableStateOf(false)
    var aboutExpanded by mutableStateOf(false)

    // scroll state
    val settingsScrollState = ScrollState(0)
    val homeScrollState = ScrollState(0)
    val fduHoleScrollState = ScrollState(0)
    val danKeScrollState = ScrollState(0)
    val timeTableScrollState = ScrollState(0)

    val stateHolders = listOf(
        fudanStateHolder,
    )

    init {
        // bind StateHolders scope and start coroutine
        stateHolders.forEach {
            it.scope = viewModelScope
            it.start()
        }
    }

    fun setDarkTheme(isDarkTheme: Boolean?) {
        viewModelScope.launch {
            settingsRepository.darkTheme.set(isDarkTheme)
        }
    }
}