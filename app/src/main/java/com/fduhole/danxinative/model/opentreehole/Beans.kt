package com.fduhole.danxinative.model.opentreehole

import kotlinx.serialization.Serializable

@Serializable
data class OTVerifyCode(
    val code: String,
)

@Serializable
data class OTRegisterInfo(
    val password: String,
    val email: String,
    val verification: Int,
)

@Serializable
data class OTLoginInfo(
    val password: String,
    val email: String,
)
@Serializable
data class OTNewHole(
    val tags: List<OTTag>,
    val specialTag: String?,
    val content: String,
)