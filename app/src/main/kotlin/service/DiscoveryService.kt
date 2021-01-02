package com.vitaliykharchenko.intouch.service

import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import kotlinx.coroutines.flow.Flow

interface DiscoveryService {
    fun getPeersStateFlow(): Flow<PeersState>
}

sealed class PeersState {
    data class Waiting(val needPermissions: List<Permission>) : PeersState()
    data class Data(val peers: List<Peer>) : PeersState()
    class Error(val desc: ErrorDescription) : PeersState()
}
