package com.kharchenkovitaliy.intouch.service.server

import com.kharchenkovitaliy.intouch.shared.Result
import com.kharchenkovitaliy.intouch.shared.tryCatch
import kotlinx.coroutines.*
import java.net.ServerSocket
import java.util.concurrent.Executors

class ServerServiceImpl : ServerService {
    private val dispatcher: ExecutorCoroutineDispatcher =
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private var serverSocket: ServerSocket? = null

    override suspend fun start(): Result<Port, Exception> =
        withContext(dispatcher) {
            tryCatch {
                val serverSocket = ServerSocket(0)
                this@ServerServiceImpl.serverSocket = serverSocket
                Port(serverSocket.localPort)
            }
        }

    override suspend fun stop() {
        withContext(dispatcher) {
            serverSocket?.close()
        }
    }

}