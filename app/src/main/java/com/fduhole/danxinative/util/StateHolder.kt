package com.fduhole.danxinative.util

import kotlinx.coroutines.CoroutineScope

/**
 * hold UI State, launch startup coroutines by a [androidx.lifecycle.ViewModel]
 */
open class StateHolder {
    lateinit var scope: CoroutineScope
    open fun start() {}
}