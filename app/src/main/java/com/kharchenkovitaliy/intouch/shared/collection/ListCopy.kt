package com.kharchenkovitaliy.intouch.shared.collection

inline class ListCopySpec<E>(
    private val mutableList: MutableList<E>
) {
    infix fun E.setAt(index: Int) {
        mutableList[index] = this
    }

    infix fun ((E) -> Boolean).replaceOn(elem: E) {
        val predicate = this
        mutableList.forEachIndexed { index, e ->
            if (predicate(e)) {
                mutableList[index] = elem
            }
        }
    }

    infix fun E.replaceOn(elem: E) {
        val oldElem = this
        mutableList.forEachIndexed { index, e ->
            if (oldElem == e) {
                mutableList[index] = elem
            }
        }
    }
}

inline fun <E> List<E>.copy(block: ListCopySpec<E>.() -> Unit): List<E> {
    val listCopy = toMutableList()
    val spec = ListCopySpec(listCopy)
    spec.block()
    return listCopy
}
