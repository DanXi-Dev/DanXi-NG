package com.fduhole.danxinative.model

import kotlinx.serialization.Serializable

@Serializable
data class DiningInfoItem(val name: String, val current: Int, val highest: Int)
