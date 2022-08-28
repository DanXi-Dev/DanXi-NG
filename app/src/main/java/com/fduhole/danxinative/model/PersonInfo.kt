package com.fduhole.danxinative.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonInfo(val name: String, val id: String, val password: String){
    override fun toString(): String {
        return String.format("%s (%s)", name, id)
    }
}