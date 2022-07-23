package com.fduhole.danxinative.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.repository.fdu.EhallRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : ViewModel(), KoinComponent {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val ehallRepository: EhallRepository by inject()
    fun onIdChanged(x: String) = _uiState.update { it.copy(idErrorText = if (x.isEmpty()) "ID 不能为空" else null) }

    fun onPasswordChanged(x: String) = _uiState.update { it.copy(passwordErrorText = if (x.isEmpty()) "密码不能为空" else null) }

    fun onLogin(id: String, password: String) {
        // If error exists, do not login
        if (_uiState.value.idErrorText != null || _uiState.value.passwordErrorText != null) return

        _uiState.update { it.copy(loggingIn = true) }
        viewModelScope.launch {
            println(ehallRepository.getStudentInfo(PersonInfo("", id, password)))
            _uiState.update { it.copy(loggingIn = false) }
        }
    }
}