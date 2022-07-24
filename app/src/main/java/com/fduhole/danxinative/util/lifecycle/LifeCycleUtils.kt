package com.fduhole.danxinative.util.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

inline fun <T, R> StateFlow<T>.watch(scope: CoroutineScope, crossinline selector: suspend (value: T) -> R, collector: FlowCollector<R>): Job =
    scope.launch { map(selector).distinctUntilChanged().collect(collector) }