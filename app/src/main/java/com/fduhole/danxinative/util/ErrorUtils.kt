package com.fduhole.danxinative.util

import android.content.Context
import android.content.res.Resources
import androidx.fragment.app.Fragment

/*
An exception that is, given string resources, able to explain itself with localized messages.
* */
abstract class ExplainableException : Exception() {
    abstract fun explain(context: Resources): String
}

/**
 * UnsuitableTimeException is related to Dining Crowdedness.
 */
class UnsuitableTimeException : Exception() {
}

class ErrorUtils {
    companion object {
        fun describeError(context: Context, error: Throwable): String = describeError(context.resources, error)

        fun describeError(context: Fragment, error: Throwable): String = describeError(context.resources, error)

        fun describeError(context: Resources, error: Throwable): String {
            if (error is ExplainableException) {
                return error.explain(context)
            }
            return error.localizedMessage ?: error.message ?: error.toString()
        }
    }
}