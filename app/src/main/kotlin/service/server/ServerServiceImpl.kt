package com.vitaliykharchenko.intouch.service.server

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.util.concurrent.Executors
import javax.inject.Inject

class ServerServiceImpl @Inject constructor() : ServerService {
    private val dispatcher: ExecutorCoroutineDispatcher =
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private var serverSocket: ServerSocket? = null

    override suspend fun start(): Result<Port, Exception> =
        withContext(dispatcher) {
            try {
                val serverSocket = ServerSocket(0)
                this@ServerServiceImpl.serverSocket = serverSocket
                Ok(Port(serverSocket.localPort))
            } catch (e: Exception) {
                Err(e)
            }
        }

    override suspend fun stop() {
        withContext(dispatcher) {
            serverSocket?.close()
        }
    }

}