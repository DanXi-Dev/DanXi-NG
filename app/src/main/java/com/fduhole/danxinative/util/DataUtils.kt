package com.fduhole.danxinative.util

object DataUtils {
    fun <T, U, V> zipToTriple(first: List<T>, second: List<U>, third: List<V>)
    : List<Triple<T, U, V>> {
        val minSize =
            if (first.size <= second.size && first.size <= third.size) first.size
            else if (second.size <= first.size && second.size <= third.size) second.size
            else third.size
        val list = ArrayList<Triple<T, U, V>>(minSize)
        for (i in 0 until minSize) {
            list.add(Triple(first[i], second[i], third[i]))
        }
        return list
    }
}