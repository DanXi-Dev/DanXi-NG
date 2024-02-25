package com.fduhole.danxi.model.fdu

import kotlinx.serialization.Serializable

@Serializable
data class UISInfo(
    val id: String,
    val password: String,
    val name: String = "",
) {
    override fun toString(): String = String.format("%s (%s)", name, id)
}
