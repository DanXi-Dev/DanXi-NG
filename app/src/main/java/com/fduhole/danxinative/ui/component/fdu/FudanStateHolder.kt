package com.fduhole.danxinative.ui.component.fdu

import com.fduhole.danxinative.repository.FDUUISInfo
import com.fduhole.danxinative.repository.UserPreferenceRepository
import com.fduhole.danxinative.repository.emptyFDUUISInfo
import com.fduhole.danxinative.repository.fdu.EhallRepository
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.ui.FDUUISViewState
import com.fduhole.danxinative.ui.component.fdu.feature.AAONoticesFeature
import com.fduhole.danxinative.ui.component.fdu.feature.ECardFeature
import com.fduhole.danxinative.ui.component.fdu.feature.LibraryAttendanceFeature
import com.fduhole.danxinative.ui.component.fdu.feature.QRCodeFeature
import com.fduhole.danxinative.util.LoginState
import com.fduhole.danxinative.util.StateHolder
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class FudanStateHolder @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val globalState: GlobalState,
    private val ehallRepository: EhallRepository,

    val fudanECardFeature: ECardFeature,
    aaoNoticesFeature: AAONoticesFeature,
    libraryAttendanceFeature: LibraryAttendanceFeature,
    qrCodeFeature: QRCodeFeature,
): StateHolder() {

    val features = listOf<Feature<*>>(
        fudanECardFeature,
        aaoNoticesFeature,
        libraryAttendanceFeature,
        qrCodeFeature,
    )

    private val _fduUISState = MutableStateFlow<LoginState<out FDUUISViewState>>(LoginState.NotLogin)
    val fduState = _fduUISState.asStateFlow()

    override fun start() {
        features.forEach {
            it.scope = scope
            it.start()
        }
        scope.launch {
            globalState.fduUISInfo.collect { info ->
                if (info.isEmpty()) {
                    if (_fduUISState.value !is LoginState.Error) {
                        _fduUISState.update { LoginState.NotLogin }
                    }
                } else {
                    if (_fduUISState.value is LoginState.NotLogin) {
                        loginFDUUIS(info.id, info.password)
                    }
                }
            }
        }
    }

    fun loginFDUUIS(id: String, password: String) {
        // If error exists, do not login
        if (id.isEmpty() || password.isEmpty()) return

        _fduUISState.value = LoginState.Loading
        scope.launch {
            try {
                val studentInfo = ehallRepository.getStudentInfo(id, password)
                val fduUISInfo = FDUUISInfo(id, password)
                userPreferenceRepository.setFDUUISInfo(fduUISInfo)
                _fduUISState.update { LoginState.Success(FDUUISViewState(id, password, studentInfo.name)) }
            } catch (e: Throwable) {
                userPreferenceRepository.setFDUUISInfo(emptyFDUUISInfo)
                _fduUISState.update { LoginState.Error(e) }
            }
        }
    }
}