package com.fduhole.danxinative.util

sealed class LoginState<T> {
    data object NotLogin : LoginState<Nothing>()
    data object Loading : LoginState<Nothing>()
    data class Success<T>(val data: T) : LoginState<T>()
    data class Error(val error: Throwable) : LoginState<Nothing>()
}