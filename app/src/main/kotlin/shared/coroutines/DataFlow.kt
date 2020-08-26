package com.vitaliykharchenko.intouch.shared.coroutines

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

interface DataFlow<T> : BroadcastChannel<T>, Flow<T> {
    val data: T
    val dataOrNull: T?
}

fun <T> DataFlow(data: T): DataFlow<T> =
    ConflatedBroadcastChannel(data).asDataFlow()

fun <T> DataFlow(): DataFlow<T> =
    ConflatedBroadcastChannel<T>().asDataFlow()

private fun <T> ConflatedBroadcastChannel<T>.asDataFlow(): DataFlow<T> {
    val channel = this
    val flow = channel.asFlow()
    return object : DataFlow<T>, BroadcastChannel<T> by channel, Flow<T> by flow {
        override val data: T get() = channel.value
        override val dataOrNull: T? get() = channel.valueOrNull
    }
}


