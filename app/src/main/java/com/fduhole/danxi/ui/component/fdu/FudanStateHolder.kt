package com.fduhole.danxi.ui.component.fdu

import com.fduhole.danxi.model.fdu.UISInfo
import com.fduhole.danxi.repository.fdu.EhallRepository
import com.fduhole.danxi.repository.settings.SettingsRepository
import com.fduhole.danxi.ui.component.fdu.feature.AAONoticesFeature
import com.fduhole.danxi.ui.component.fdu.feature.ECardFeature
import com.fduhole.danxi.ui.component.fdu.feature.LibraryAttendanceFeature
import com.fduhole.danxi.ui.component.fdu.feature.QRCodeFeature
import com.fduhole.danxi.util.LoginStatus
import com.fduhole.danxi.util.StateHolder
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class FudanStateHolder @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val ehallRepository: EhallRepository,

    val fudanECardFeature: ECardFeature,
    aaoNoticesFeature: AAONoticesFeature,
    libraryAttendanceFeature: LibraryAttendanceFeature,
    qrCodeFeature: QRCodeFeature,
) : StateHolder() {

    val features = listOf<Feature<*>>(
        fudanECardFeature,
        aaoNoticesFeature,
        libraryAttendanceFeature,
        qrCodeFeature,
    )

    private val _fduUISState = MutableStateFlow<LoginStatus<out UISInfo>>(LoginStatus.NotLogin)
    val fduState = _fduUISState.asStateFlow()

    override fun start() {
        features.forEach {
            it.scope = scope
            it.start()
        }
        scope.launch {
            settingsRepository.uisInfo.flow.collect { info ->
                if (info == null) {
                    if (_fduUISState.value !is LoginStatus.Error) {
                        _fduUISState.update { LoginStatus.NotLogin }
                    }
                } else {
                    if (_fduUISState.value is LoginStatus.NotLogin) {
                        loginFDUUIS(info.id, info.password)
                    }
                }
            }
        }
    }

    fun loginFDUUIS(id: String, password: String) {
        // If error exists, do not login
        if (id.isEmpty() || password.isEmpty()) return

        _fduUISState.value = LoginStatus.Loading
        scope.launch {
            try {
                val studentInfo = ehallRepository.getStudentInfo(id, password)
                val uisInfo = UISInfo(id, password, studentInfo.name)
                settingsRepository.uisInfo.set(uisInfo)
                _fduUISState.update { LoginStatus.Success(uisInfo) }
            } catch (e: Throwable) {
                settingsRepository.uisInfo.remove()
                _fduUISState.update { LoginStatus.Error(e) }
            }
        }
    }
}