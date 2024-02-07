package com.fduhole.danxinative.model.fdu

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CardRecord(
    val time: Instant,
    val type: String,
    val location: String,
    val amount: String,
    val balance: String,
)

