package com.fduhole.danxinative.base

import kotlinx.coroutines.CoroutineScope


abstract class Feature {
    lateinit var callback: () -> Unit
    lateinit var featureScope: CoroutineScope
    abstract fun getClickable(): Boolean
    open fun onClick() {}
    open fun inProgress(): Boolean = false
    open fun getIconId(): Int? = null
    abstract fun getTitle(): String
    abstract fun getSubTitle(): String
    fun initFeature(callback: () -> Unit, featureScope: CoroutineScope) {
        this.callback = callback
        this.featureScope = featureScope
    }

    open fun onCreated() {}
    fun notifyRefresh() = callback.invoke()
}