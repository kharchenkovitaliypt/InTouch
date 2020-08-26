package com.vitaliykharchenko.intouch.shared.collection

fun <T> List<T>.removeFirst(predicate: (T) -> Boolean): List<T> =
    find(predicate)
        ?.let { this@removeFirst - it }
        ?: this