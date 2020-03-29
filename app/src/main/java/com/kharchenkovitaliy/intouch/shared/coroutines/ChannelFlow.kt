package com.kharchenkovitaliy.intouch.shared.coroutines

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

interface ChannelFlow<E> : SendChannel<E>, Flow<E>

fun <E> StatefulChannelFlow(): ChannelFlow<E> =
    ConflatedBroadcastChannel<E>().asChannelFlow()

fun <E> StatefulChannelFlow(elem: E): ChannelFlow<E> =
    ConflatedBroadcastChannel(elem).asChannelFlow()

private fun <E> BroadcastChannel<E>.asChannelFlow(): ChannelFlow<E> {
    val channel = this
    val flow = channel.asFlow()
    return object : ChannelFlow<E>, SendChannel<E> by channel, Flow<E> by flow { }
}

