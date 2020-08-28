package com.vitaliykharchenko.intouch.service.nsd

import android.net.nsd.NsdServiceInfo
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onSuccess
import com.vitaliykharchenko.intouch.service.ErrorService
import com.vitaliykharchenko.intouch.service.PeerServerService
import com.vitaliykharchenko.intouch.service.server.ServerService
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import com.vitaliykharchenko.intouch.shared.coroutines.DataFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NsdPeerServerService @Inject constructor(
    private val serviceType: NsdServiceType,
    private val nsdService: CoroutineNsdManager,
    private val serverService: ServerService,
    private val errorService: ErrorService
) : PeerServerService {
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main

    override val serviceFlow = DataFlow<NsdServiceInfo?>(null)

    override suspend fun start(name: String): Result<Unit, ErrorDescription> {
        return withContext(dispatcher) {
            startInternal(name)
                .onSuccess { service ->
                    serviceFlow.offer(service)
                }
                .map { Unit }
        }
    }

    private suspend fun startInternal(name: String): Result<NsdServiceInfo, ErrorDescription> {
        val port = serverService.start()
            .mapError(errorService::getDescription)
            .getOrElse { error -> return Err(error) }

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