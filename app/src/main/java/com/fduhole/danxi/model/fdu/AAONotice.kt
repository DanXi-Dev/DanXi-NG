package com.fduhole.danxi.model.fdu

import kotlinx.serialization.Serializable

@Serializable
data class AAONotice(
    val title: String,
    val url: String,
    val time: String,
)
