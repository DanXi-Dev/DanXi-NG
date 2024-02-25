package com.fduhole.danxi.util

import io.ktor.http.ParametersBuilder
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun String.between(start: String, end: String, headGreedy: Boolean = true): String? {
    val startFirstPos = indexOf(start)
    if (startFirstPos < 0) return null
    if (headGreedy) {
        val endFirstPos = indexOf(end, startFirstPos + start.length)
        if (endFirstPos < 0) return null
        return substring(startFirstPos + start.length, endFirstPos)
    } else {
        val startLastEndPos = lastIndexOf(start) + start.length
        val endFirstPos = indexOf(end, startLastEndPos)
        if (endFirstPos < 0) return null
        return substring(startLastEndPos, endFirstPos)
    }
}

fun ParametersBuilder.appendAll(map: Map<String, String>) {
    for (entry in map) {
        append(entry.key, entry.value)
    }
}

fun Instant.toDateTimeString(formatter: DateTimeFormatter): String =
    formatter.format(toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime())

fun Instant.toDateTimeString(formatStyle: FormatStyle = FormatStyle.MEDIUM): String =
    toDateTimeString(DateTimeFormatter.ofLocalizedDateTime(formatStyle))

fun Instant.toDateTimeString(formatString: String): String =
    toDateTimeString(DateTimeFormatter.ofPattern(formatString))