package com.fduhole.danxinative.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxinative.R
import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.repository.fdu.EhallRepository
import com.fduhole.danxinative.state.GlobalState
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
    private val globalState: GlobalState by inject()
    fun onIdChanged(x: String) = _uiState.update { it.copy(idErrorId = if (x.isEmpty()) R.string.id_non_empty else null) }

    fun onPasswordChanged(x: String) = _uiState.update { it.copy(passwordErrorId = if (x.isEmpty()) R.string.password_non_empty else null) }

    fun logIn(id: String?, password: String?) {
        // If error exists, do not login
        onIdChanged(id.orEmpty())
        onPasswordChanged(password.orEmpty())
        if (id.isNullOrEmpty() || password.isNullOrEmpty()) return

        _uiState.update { it.copy(loggingIn = true) }
        viewModelScope.launch {
            try {
                val studentInfo = ehallRepository.getStudentInfo(PersonInfo("", id, password))
                globalState.person = PersonInfo(studentInfo.name!!, id, password)
                _uiState.update { it.copy(logged = true) }
            } catch (e: Throwable) {
                _uiState.update { it.copy(loginError = e) }
            }

            _uiState.update { it.copy(loggingIn = false) }
        }
    }
}