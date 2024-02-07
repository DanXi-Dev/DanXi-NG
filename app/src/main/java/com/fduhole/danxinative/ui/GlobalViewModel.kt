package com.fduhole.danxinative.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxinative.model.opentreehole.OTJWTToken
import com.fduhole.danxinative.repository.UserPreferenceRepository
import com.fduhole.danxinative.repository.fdu.AAORepository
import com.fduhole.danxinative.repository.fdu.ECardRepository
import com.fduhole.danxinative.repository.fdu.EhallRepository
import com.fduhole.danxinative.repository.fdu.LibraryRepository
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.ui.component.fdu.FudanStateHolder
import com.fduhole.danxinative.util.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GlobalViewState(
    val isDarkTheme: Boolean? = null,
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
    private val userPreferenceRepository: UserPreferenceRepository,
    private val globalState: GlobalState,
    private val eCardRepository: ECardRepository,
    private val ehallRepository: EhallRepository,
    private val aaoRepository: AAORepository,
    private val libraryRepository: LibraryRepository,
    val fudanStateHolder: FudanStateHolder,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GlobalViewState())
    private val _fduHoleState = MutableStateFlow<LoginState<out FDUHoleViewState>>(LoginState.NotLogin)

    val uiState = _uiState.asStateFlow()
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
        globalState,
        fudanStateHolder,
    )

    init {
        // bind StateHolders scope and start coroutine
        stateHolders.forEach {
            it.scope = viewModelScope
            it.start()
        }

        // launch self coroutines
        viewModelScope.run {

            launch {
                userPreferenceRepository.isDarkTheme.collect {
                    _uiState.value = _uiState.value.copy(isDarkTheme = it)
                }
            }
        }
    }

    fun setDarkTheme(isDarkTheme: Boolean?) {
        viewModelScope.launch {
            userPreferenceRepository.setDarkTheme(isDarkTheme)
        }
    }
}