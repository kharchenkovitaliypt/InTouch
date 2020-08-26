package com.vitaliykharchenko.intouch.service

import android.net.nsd.NsdServiceInfo
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onSuccess
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.model.PeerId
import com.vitaliykharchenko.intouch.service.nsd.CoroutineNsdManager
import com.vitaliykharchenko.intouch.service.nsd.NsdServiceType
import com.vitaliykharchenko.intouch.service.nsd.ServiceEvent
import com.vitaliykharchenko.intouch.service.nsd.description
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.scan
import javax.inject.Inject

interface PeerDiscoveryService {
    val peersFlow: Flow<List<Peer>>
    suspend fun start(): Result<Unit, ErrorDescription>
    suspend fun stop(): Result<Unit, ErrorDescription>
}

class PeerDiscoveryServiceImpl @Inject constructor(
    private val serviceType: NsdServiceType,
    private val nsdService: CoroutineNsdManager
) : PeerDiscoveryService {

    private val serviceEventChannel = ConflatedBroadcastChannel<Flow<ServiceEvent>?>(null)
    override val peersFlow: Flow<List<Peer>> = serviceEventChannel.asFlow()
        .flatMapLatest { flow ->
            flow?.scanPeers() ?: flowOf(emptyList())
        }

    private fun Flow<ServiceEvent>.scanPeers(): Flow<List<Peer>> =
        scan(emptyList()) { peers: List<Peer>, event: ServiceEvent ->
            if (peers.isEmpty()) {
                event.allServices.map(NsdServiceInfo::toPeer)
            } else {
                when (event) {
                    is ServiceEvent.Found -> peers + event.service.toPeer()
                    is ServiceEvent.Lost -> peers - event.service.toPeer()
                }
            }
        }

    override suspend fun start(): Result<Unit, ErrorDescription> =
        nsdService.startDiscovery(serviceType)
            .onSuccess { events ->
                serviceEventChannel.offer(events)
            }
            .map { Unit }
            .mapError { it.description }

    override suspend fun stop(): Result<Unit, ErrorDescription> =
        nsdService.stopDiscovery(serviceType)
            .mapError { it.description }
            .also {
                serviceEventChannel.offer(null)
            }
}

private fun NsdServiceInfo.toPeer() =
    Peer(id = getPeerId(), name = "$serviceName($host:$port)")

private fun NsdServiceInfo.getPeerId() =
    PeerId(serviceType + serviceName)
