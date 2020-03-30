package com.kharchenkovitaliy.intouch.service

import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.service.nsd.CoroutineNsdManager
import com.kharchenkovitaliy.intouch.service.nsd.NsdServiceType
import com.kharchenkovitaliy.intouch.service.nsd.description
import com.kharchenkovitaliy.intouch.service.server.ServerService
import com.kharchenkovitaliy.intouch.service.shared.ErrorDescription
import com.kharchenkovitaliy.intouch.shared.Result
import com.kharchenkovitaliy.intouch.shared.coroutines.ConflatedChannelFlow
import com.kharchenkovitaliy.intouch.shared.getOrElse
import com.kharchenkovitaliy.intouch.shared.map
import com.kharchenkovitaliy.intouch.shared.mapError
import com.kharchenkovitaliy.intouch.shared.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PeerServerService {
    val serviceFlow: Flow<NsdServiceInfo?>
    suspend fun start(name: String): Result<Unit, ErrorDescription>
    suspend fun stop()
}

class PeerServerServiceImpl @Inject constructor(
    private val serviceType: NsdServiceType,
    private val nsdService: CoroutineNsdManager,
    private val serverService: ServerService,
    private val errorService: ErrorService
) : PeerServerService {
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main

    override val serviceFlow = ConflatedChannelFlow<NsdServiceInfo?>(null)

    override suspend fun start(name: String): Result<Unit, ErrorDescription> =
        withContext(dispatcher) {
            startInternal(name)
                .onSuccess { service ->
                    serviceFlow.offer(service)
                }
                .map { Unit }
        }

    private suspend fun startInternal(name: String): Result<NsdServiceInfo, ErrorDescription> {
        val port = serverService.start()
            .mapError(errorService::getDescription)
            .getOrElse { error -> return Result.failure((error)) }

        val serviceInfo = NsdServiceInfo().also {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            it.serviceName = name
            it.serviceType = serviceType.value
            it.port = port.value
        }
        return nsdService.registerService(serviceInfo)
            .mapError { it.description }
    }

    override suspend fun stop() {
        withContext(dispatcher) {
            serviceFlow.first()?.let { service ->
                nsdService.unregisterService(service)
            }
            serviceFlow.offer(null)
        }
    }
}