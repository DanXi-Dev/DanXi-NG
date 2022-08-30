package com.fduhole.danxinative.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonInfo(val name: String, val id: String, val password: String){
    override fun toString(): String = String.format("%s (%s)", name, id)
}