package com.fduhole.danxinative.model.fdu

import kotlinx.serialization.Serializable

@Serializable
data class UISInfo(
    val id: String,
    val password: String,
    val name: String = "",
)
