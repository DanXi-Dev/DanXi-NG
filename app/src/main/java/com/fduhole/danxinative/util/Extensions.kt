package com.fduhole.danxinative.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import okhttp3.FormBody
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

fun FormBody.Builder.addMap(map: Map<String, String>, alreadyEncoded: Boolean = false): FormBody.Builder {
    for (entry in map) {
        if (alreadyEncoded)
            addEncoded(entry.key, entry.value)
        else
            add(entry.key, entry.value)
    }
    return this
}

fun Instant.toDateTimeString(formatter: DateTimeFormatter): String =
    formatter.format(toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime())

fun Instant.toDateTimeString(formatStyle: FormatStyle = FormatStyle.MEDIUM): String =
    toDateTimeString(DateTimeFormatter.ofLocalizedDateTime(formatStyle))

fun Instant.toDateTimeString(formatString: String): String =
    toDateTimeString(DateTimeFormatter.ofPattern(formatString))