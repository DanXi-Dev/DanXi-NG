package com.fduhole.danxi.model.opentreehole

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OTDivision(
    @SerialName("division_id")
    val divisionId: Int?,
    val name: String?,
    val description: String?,
    @SerialName("pinned")
    val pinnedHoles: List<OTHole>?,
)

@Serializable
data class OTHole(
    @SerialName("hole_id")
    val holdId: Int?,
    @SerialName("division_id")
    val divisionId: Int?,
    @SerialName("time_updated")
    val updatedTime: String?,
    @SerialName("time_created")
    val createdTime: String?,
    val tags: List<OTTag>?,
    @SerialName("view")
    val viewNum: Int?,
    @SerialName("reply")
    val replyNum: Int?,
    val floors: OTFloors?,
    val hidden: Boolean?,
)

@Serializable
data class OTFloors(
    @SerialName("first_floor")
    val firstFloor: OTFloor?,
    @SerialName("last_floor")
    val lastFloor: OTFloor?,
    @SerialName("prefetch")
    val prefetchFloors: List<OTFloor>,
)

@Serializable
data class OTTag(
    @SerialName("tag_id")
    val tagId: Int?,
    val temperature: Int?,
    val name: String?,
)

@Serializable
data class OTFloor(
    @SerialName("floor_id")
    val floorId: Int?,
    @SerialName("hole_id")
    val holeId: Int?,
    val content: String?,
    @SerialName("anonyname")
    val anonymousName: String?,
    @SerialName("time_updated")
    val updatedTime: String?,
    @SerialName("time_created")
    val createdTime: String?,
    @SerialName("special_tag")
    val specialTag: String?,
    val deleted: Boolean?,
    @SerialName("is_me")
    val isMe: Boolean?,
    val liked: Boolean?,
    @SerialName("fold_reason")
    val foldReason: List<String>?,
    val history: List<OTHistory>?,
    @SerialName("like")
    val likeNum: Int?,
)

@Serializable
data class OTHistory(
    val content: String?,
    @SerialName("altered_by")
    val alteredBy: Int?,
    @SerialName("altered_time")
    val alteredTime: String?,
)

@Serializable
data class OTJWTToken(
    val access: String?,
    val refresh: String?,
)

@Serializable
data class OTMessage(
    @SerialName("message_id")
    val messageId: Int?,
    val message: String?,
    val code: String?,
    @SerialName("time_created")
    val createdTime: String?,
    @SerialName("has_read")
    val hasRead: Boolean?,
)

@Serializable
data class OTReport(
    @SerialName("report_id")
    val reportId: Int?,
    val reason: String?,
    val content: String?,
    val floor: OTFloor?,
    @SerialName("hole_id")
    val holeId: Int?,
    @SerialName("time_updated")
    val updatedTime: String?,
    @SerialName("time_created")
    val createdTime: String?,
    @SerialName("dealed")
    val dealt: Boolean?,
    @SerialName("dealed_by")
    val dealtBy: String?,
)

@Serializable
data class OTUser(
    @SerialName("user_id")
    val userId: Int?,
    val nickname: String?,
    val favorites: List<Int>?,
    val permission: OTUserPermission?,
    val config: OTUserConfig?,
    @SerialName("joined_time")
    val joinedTime: String?,
    @SerialName("is_admin")
    val isAdmin: Boolean?,
)

@Serializable
data class OTUserPermission(
    val silent: Map<Int, String>?,
    val admin: String?,
)

@Serializable
data class OTUserConfig(
    val notify: List<String>?,
    @SerialName("show_folded")
    val foldType: String?,
)