package com.kharchenkovitaliy.intouch.service

import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.model.Peer
import com.kharchenkovitaliy.intouch.model.PeerId
import com.kharchenkovitaliy.intouch.service.nsd.NsdService
import com.kharchenkovitaliy.intouch.service.nsd.NsdServiceType
import com.kharchenkovitaliy.intouch.service.nsd.ServiceEvent
import com.kharchenkovitaliy.intouch.service.nsd.description
import com.kharchenkovitaliy.intouch.service.shared.ErrorDescription
import com.kharchenkovitaliy.intouch.shared.Result
import com.kharchenkovitaliy.intouch.shared.map
import com.kharchenkovitaliy.intouch.shared.mapError
import com.kharchenkovitaliy.intouch.shared.onSuccess
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface PeerDiscoveryService {
    val peersFlow: Flow<List<Peer>>
    suspend fun start(): Result<Unit, ErrorDescription>
    suspend fun stop(): Result<Unit, ErrorDescription>
}

class PeerDiscoveryServiceImpl @Inject constructor(
    private val serviceType: NsdServiceType,
    private val nsdService: NsdService
) : PeerDiscoveryService {
    private val eventChannel: BroadcastChannel<Flow<ServiceEvent>?> = ConflatedBroadcastChannel(null)

    override val peersFlow: Flow<List<Peer>> = eventChannel.asFlow()
        .flatMapLatest { flow ->
            flow?.scan(emptyList(), ::process)
                ?: flowOf(emptyList())
        }

    private suspend fun process(list: List<Peer>, event: ServiceEvent): List<Peer> =
        when (event) {
            is ServiceEvent.Found -> {
                list + event.service.toPeer()
            }
            is ServiceEvent.Lost -> {
                list - event.service.toPeer()
            }
        }

    override suspend fun start(): Result<Unit, ErrorDescription> =
        nsdService.startDiscovery(serviceType)
            .onSuccess { events ->
                eventChannel.offer(events)
            }
            .map { Unit }
            .mapError { it.description }

    override suspend fun stop(): Result<Unit, ErrorDescription> =
        nsdService.stopDiscovery(serviceType)
            .mapError { it.description }
            .also {
                eventChannel.offer(null)
            }
}

private fun NsdServiceInfo.toPeer() =
    Peer(id = getPeerId(), name = serviceName)

private fun NsdServiceInfo.getPeerId() =
    PeerId(serviceType + serviceName)
