package com.kharchenkovitaliy.intouch.service

import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.model.Peer
import com.kharchenkovitaliy.intouch.model.PeerId
import com.kharchenkovitaliy.intouch.service.nsd.*
import com.kharchenkovitaliy.intouch.service.server.ServerService
import com.kharchenkovitaliy.intouch.service.shared.ErrorDescription
import com.kharchenkovitaliy.intouch.shared.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PeerService @Inject constructor(
    private val nsdService: NsdService,
    private val serverService: ServerService,
    private val errorService: ErrorService
) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main

    private val serviceType = NsdServiceType("_nsd_touch._tcp")

    private var onPeersChanged: ((List<Peer>) -> Unit)? = null

    private val serverServiceChannel = ConflatedBroadcastChannel<NsdServiceInfo?>()
        .apply { offer(null) }
    val serverServiceFlow = serverServiceChannel.asFlow()

    suspend fun startServer(name: String): Result<Unit, ErrorDescription> =
        withContext(dispatcher) {
            startServerInternal(name)
                .onSuccess { service ->
                    serverServiceChannel.offer(service)
                }
                .map { Unit }
        }

    private suspend fun startServerInternal(name: String): Result<NsdServiceInfo, ErrorDescription> {
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

    suspend fun unregister() {
        withContext(dispatcher) {
            serverServiceFlow.first()?.let { service ->
                nsdService.unregisterService(service)
            }
            serverServiceChannel.offer(null)
        }
    }

    suspend fun startDiscover(
        onPeersChanged: (List<Peer>) -> Unit
    ): Result<Unit, ErrorDescription> {
        val peers = mutableListOf<Peer>()
        this.onPeersChanged = onPeersChanged
        onPeersChanged(peers)

        return nsdService.startDiscovery(
            serviceType = serviceType,
            onServiceFound = { serviceInfo ->
                peers.add(serviceInfo.toPeer())
                onPeersChanged(peers)
            },
            onServiceLost = { serviceInfo ->
                peers -= serviceInfo.toPeer()
                onPeersChanged(peers)
            }
        ).mapError { it.description }
    }

    suspend fun stopDiscovery(): Result<Unit, ErrorDescription> {
        onPeersChanged?.invoke(emptyList())
        onPeersChanged = null

        return nsdService.stopDiscovery(serviceType)
            .mapError { it.description }
    }
}

private fun NsdServiceInfo.toPeer() =
    Peer(id = getPeerId(), name = serviceName)

private fun NsdServiceInfo.getPeerId() =
    PeerId(serviceType + serviceName)
