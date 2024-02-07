package com.fduhole.danxinative.state


import com.fduhole.danxinative.repository.UserPreferenceRepository
import com.fduhole.danxinative.repository.emptyFDUUISInfo
import com.fduhole.danxinative.util.StateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalState @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
): StateHolder() {
    val fduUISInfo = MutableStateFlow(emptyFDUUISInfo)

    override fun start() {
        scope.launch(Dispatchers.IO) {
            userPreferenceRepository.fduUISInfo.collect {
                fduUISInfo.value = it
            }
        }
    }
}