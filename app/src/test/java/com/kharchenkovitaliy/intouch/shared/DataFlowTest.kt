package com.kharchenkovitaliy.intouch.shared

import com.kharchenkovitaliy.intouch.shared.coroutines.DataFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DataFlowTest {

    @Test fun statefulDataFlow() = runBlockingTest {
        val channelFlow = DataFlow<String>()

        val result1 = async { channelFlow.toList() }
        channelFlow.offer("Hello")
        channelFlow.offer("world")

        val result2 = async { channelFlow.toList() }
        channelFlow.offer("!!")
        channelFlow.close()

        assertEquals(listOf("Hello", "world", "!!"), result1.await())
        assertEquals(listOf("world", "!!"), result2.await())
    }
}