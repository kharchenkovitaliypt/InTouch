package com.vitaliykharchenko.intouch.service.nsd

import android.net.nsd.NsdServiceInfo
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.PeerDiscoveryService
import com.vitaliykharchenko.intouch.service.PeersState
import com.vitaliykharchenko.intouch.service.Permission
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NsdPeerDiscoveryService @Inject constructor(
    private val serviceType: NsdServiceType,
    private val nsdService: CoroutineNsdManager
) : PeerDiscoveryService {

    override val needPermissions: List<Permission>
        get() = TODO("Not yet implemented")

    override suspend fun getPeersStateFlow(): Flow<PeersState> {
        TODO("Not yet implemented")
    }

//    private val serviceEventChannel = ConflatedBroadcastChannel<Flow<ServiceEvent>?>(null)
//    override val peersFlow: Flow<List<Peer>> = serviceEventChannel.asFlow()
//        .flatMapLatest { flow ->
//            flow?.scanPeers() ?: flowOf(emptyList())
//        }
//
//    private fun Flow<ServiceEvent>.scanPeers(): Flow<List<Peer>> =
//        scan(emptyList()) { peers: List<Peer>, event: ServiceEvent ->
//            if (peers.isEmpty()) {
//                event.allServices.map(NsdServiceInfo::toPeer)
//            } else {
//                when (event) {
//                    is ServiceEvent.Found -> peers + event.service.toPeer()
//                    is ServiceEvent.Lost -> peers - event.service.toPeer()
//                }
//            }
//        }
//
//    override suspend fun start(): Result<Unit, ErrorDescription> =
//        nsdService.startDiscovery(serviceType)
//            .onSuccess { events ->
//                serviceEventChannel.offer(events)
//            }
//            .map { Unit }
//            .mapError { it.description }
//
//    override suspend fun stop(): Result<Unit, ErrorDescription> =
//        nsdService.stopDiscovery(serviceType)
//            .mapError { it.description }
//            .also {
//                serviceEventChannel.offer(null)
//            }
}

private fun NsdServiceInfo.toPeer() =
    Peer(id = getPeerId(), name = "$serviceName($host:$port)")

private fun NsdServiceInfo.getPeerId(): String =
    serviceType + serviceName