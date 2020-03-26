package com.kharchenkovitaliy.intouch.shared

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

interface ChannelFlow<E> : SendChannel<E>, Flow<E>

fun <E> ChannelFlow(): ChannelFlow<E> {
    val channel = ConflatedBroadcastChannel<E>()
    val flow = channel.asFlow()
    return object : ChannelFlow<E>, SendChannel<E> by channel, Flow<E> by flow { }
}

fun <E> ChannelFlow(elem: E): ChannelFlow<E> {
    val channel = ConflatedBroadcastChannel(elem)
    val flow = channel.asFlow()
    return object : ChannelFlow<E>, SendChannel<E> by channel, Flow<E> by flow { }
}
