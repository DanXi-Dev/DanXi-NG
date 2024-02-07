package com.fduhole.danxinative.model.fdu

import kotlinx.serialization.Serializable

@Serializable
data class CardPersonInfo(
    val balance: String,
    val name: String,
    val recentRecord: List<CardRecord>
)

