package com.vitaliykharchenko.intouch.shared

class ThreadChecker {
    private val thread = Thread.currentThread()

    fun check() {
        check(Thread.currentThread() == thread)
    }
}