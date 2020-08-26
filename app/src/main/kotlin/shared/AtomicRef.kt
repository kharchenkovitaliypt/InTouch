package com.vitaliykharchenko.intouch.shared

import java.util.concurrent.atomic.AtomicReference

typealias AtomicRef<V> = AtomicReference<V>

fun <V> AtomicRef<V>.updateAndGetCompat(update: (V) -> V): V {
    var prev: V
    var next: V
    do {
        prev = get()
        next = update(prev)
    } while (!compareAndSet(prev, next))
    return next
}