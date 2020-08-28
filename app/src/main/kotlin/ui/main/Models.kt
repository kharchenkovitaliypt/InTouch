package com.vitaliykharchenko.intouch.ui.main

import com.vitaliykharchenko.intouch.model.PeerId
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription

data class MainUi(
    val serverName: String,
    val peersState: PeersUiState,

    val onStartServer: () -> Unit,
    val onStopServer: () -> Unit,

    val onStartDiscovery: () -> Unit,
    val onStopDiscovery: () -> Unit
)

sealed class PeersUiState {
    object Idle : PeersUiState()
    object Waiting : PeersUiState()
    data class Data(val peers: List<PeerUi>) : PeersUiState()
    class Error(val desc: ErrorDescription) : PeersUiState()
}

data class PeerUi(
    val id: PeerId,
    val name: String,
    val onClick: () -> Unit
)