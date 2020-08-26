package com.vitaliykharchenko.intouch.shared.collection

inline class MapCopySpec<K, V>(
    private val mutableMap: MutableMap<K, V>
) {
    infix fun K.set(value: V) {
        mutableMap[this] = value
    }

    infix fun remove(key: K) {
        mutableMap -= key
    }
}

inline fun <K, V> Map<K, V>.copy(block: MapCopySpec<K, V>.() -> Unit): Map<K, V> {
    val mapCopy = toMutableMap()
    val spec = MapCopySpec(mapCopy)
    spec.block()
    return mapCopy
}
