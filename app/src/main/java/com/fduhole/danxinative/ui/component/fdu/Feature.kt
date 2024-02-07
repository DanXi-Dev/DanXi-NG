package com.fduhole.danxinative.ui.component.fdu

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.fduhole.danxinative.util.StateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class Feature<T>(
    val icon: ImageVector,
    @get:StringRes val title: Int,
    val subTitleDefault: String? = null,
    val shouldLoadData: Boolean = true,
    val hasMoreContent: Boolean = false,
    val shouldNavigateOnClick: Boolean = false,
) : StateHolder() {

    sealed interface Status<out T> {
        data object Idle : Status<Nothing>
        data object Loading : Status<Nothing>
        data class Error(val error: Throwable) : Status<Nothing>
        data class Success<out T>(
            val message: String,
            val data: T,
        ) : Status<T>
    }

    data class State<out T>(
        val clickable: Boolean,
        val state: Status<T> = Status.Idle,
        val showMoreContent: Boolean = false,
    )

    protected abstract val mUIState: MutableStateFlow<State<T>>
    val uiState: StateFlow<State<T>>
        get() = mUIState.asStateFlow()

    open fun onStart(navController: NavController) {}

    open fun onRefresh(navController: NavController) {}

    open suspend fun loading() {
        if (uiState.value.state !is Status.Loading) {
            mUIState.update { it.copy(state = Status.Loading, clickable = false) }
            loadData().onSuccess { newState ->
                mUIState.update { it.copy(state = newState, clickable = true) }
            }.onFailure { e ->
                mUIState.update { it.copy(state = Status.Error(e), clickable = true) }
            }
        }
    }

    open fun onClick(navController: NavController) {
        scope.launch {
            val showMoreContent: () -> Unit = {
                mUIState.update { it.copy(showMoreContent = true) }
            }

            when (mUIState.value.state) {
                Status.Idle -> {
                    if (shouldLoadData) {
                        loading()
                    } else {
                        if (hasMoreContent) {
                            showMoreContent()
                        } else if (shouldNavigateOnClick) {
                            navigate(navController)
                        } else {
                            loading()
                        }
                    }
                }

                is Status.Error -> {
                    if (shouldLoadData) loading()
                }

                is Status.Success -> {
                    if (hasMoreContent) {
                        showMoreContent()
                    } else if (shouldNavigateOnClick) {
                        navigate(navController)
                    } else {
                        loading()
                    }
                }

                else -> {}
            }
        }
    }

    open suspend fun loadData(): Result<Status<T>> = Result.failure(NotImplementedError())
    open val trailingContent: @Composable () -> Unit = {}
    open val moreContent: @Composable (navController: NavController) -> Unit = {}
    open fun navigate(navController: NavController) {}
}
