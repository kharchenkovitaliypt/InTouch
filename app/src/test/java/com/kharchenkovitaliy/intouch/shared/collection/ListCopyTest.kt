package com.kharchenkovitaliy.intouch.shared.collection

import org.junit.Assert.assertEquals
import org.junit.Test

class ListCopyTest {

    @Test fun copy() {
        val originList = listOf(
            "hello",
            "",
            "hell",
            "runner"
        )
        val actualList = originList.copy {
            "not empty" setAt 1
            "hell" replaceOn "good place for rest"
        }
        val expectedList = listOf(
            "hello",
            "not empty",
            "good place for rest",
            "runner"
        )
        assertEquals(expectedList, actualList)
    }
}
