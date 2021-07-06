package com.vitaliykharchenko.intouch.service.server

import com.github.michaelbull.result.Result

@JvmInline
value class Port(val value: Int)

interface ServerService {

    suspend fun start(): Result<Port, Exception>

    suspend fun stop()
}