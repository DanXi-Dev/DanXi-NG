package com.fduhole.danxi.model.fdu

import kotlinx.serialization.Serializable

@Serializable
data class LibraryInfo(
    val campusId: String,
    val campusName: String,
    val inNum: String,
    val libraryOpenTime: String,
    val placeNum: String,
)

