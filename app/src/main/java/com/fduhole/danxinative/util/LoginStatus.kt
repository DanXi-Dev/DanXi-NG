package com.fduhole.danxinative.util

sealed class LoginStatus<T> {
    data object NotLogin : LoginStatus<Nothing>()
    data object Loading : LoginStatus<Nothing>()
    data class Success<T>(val data: T) : LoginStatus<T>()
    data class Error(val error: Throwable) : LoginStatus<Nothing>()
}