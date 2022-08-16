package com.fduhole.danxinative.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CardRecord(val time: Instant, val type: String, val location: String, val payment: String)

