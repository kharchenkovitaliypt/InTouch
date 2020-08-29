package com.vitaliykharchenko.intouch.service

import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import kotlinx.coroutines.flow.Flow

typealias Permission = String

interface PeerDiscoveryService {
    val needPermissions: List<Permission>
    suspend fun getPeersStateFlow(): Flow<PeersState>
}

sealed class PeersState {
    object Waiting : PeersState()
    data class Data(val peers: List<Peer>) : PeersState()
    class Error(val desc: ErrorDescription) : PeersState()
}
