package com.kharchenkovitaliy.intouch.service.server

import com.github.michaelbull.result.Result

inline class Port(val value: Int)

interface ServerService {

    suspend fun start(): Result<Port, Exception>

    suspend fun stop()
}