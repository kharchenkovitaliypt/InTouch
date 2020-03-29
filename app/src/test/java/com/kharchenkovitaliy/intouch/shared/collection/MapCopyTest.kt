package com.kharchenkovitaliy.intouch.shared.collection

import org.junit.Assert.assertEquals
import org.junit.Test

class MapCopyTest {

    @Test fun copy() {
        val testKey = Any()
        val originMap = mapOf<Any, Any>(
            "hello" to 3,
            49 to true,
            "runner" to 98,
            testKey to false
        )
        val actualMap = originMap.copy {
            testKey set true
            49 set "newValue"
        }
        val expectedMap = mapOf(
            "hello" to 3,
            49 to "newValue",
            "runner" to 98,
            testKey to true
        )
        assertEquals(expectedMap, actualMap)
    }
}
