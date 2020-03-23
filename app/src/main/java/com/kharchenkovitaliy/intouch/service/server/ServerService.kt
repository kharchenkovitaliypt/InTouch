package com.kharchenkovitaliy.intouch.service.server

import com.kharchenkovitaliy.intouch.shared.Result

inline class Port(val value: Int)

interface ServerService {

    suspend fun start(): Result<Port, Exception>

    suspend fun stop()
}