package com.vitaliykharchenko.intouch.ui.main

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.model.PeerId
import com.vitaliykharchenko.intouch.service.PeerDiscoveryService
import com.vitaliykharchenko.intouch.service.PeerServerService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val peerServerService: PeerServerService,
    private val peerDiscoveryService: PeerDiscoveryService
) : ViewModel() {

    private val _state = MutableStateFlow(
        MainUiState(
            serverName = "",
            peers = emptyList(),
            onStartServer = {
                viewModelScope.launch {
                    peerServerService.start(Build.DEVICE)
                }
            },
            onStopServer = {
                viewModelScope.launch {
                    peerServerService.stop()
                }
            },
            onStartDiscovery = {
                viewModelScope.launch {
                    peerDiscoveryService.start()
                }
            },
            onStopDiscovery = {
                viewModelScope.launch {
                    peerDiscoveryService.stop()
                }
            }
        )
    )
    val state: StateFlow<MainUiState> = _state

    init {
        combine(
            peerServerService.serviceFlow.map { it?.serviceName ?: "????" },
            peerDiscoveryService.peersFlow.map { list ->
                list.map { it.toPeerUi() }
            }
        ) { serviceName, peers ->
            _state.value = state.value.copy(
                serverName = serviceName,
                peers = peers
            )
        }.launchIn(viewModelScope)
    }

    private fun Peer.toPeerUi() =
        PeerUi(id, name, onClick = {
            // Just do it
        })
}

data class MainUiState(
    val serverName: String,
    val peers: List<PeerUi>,

    val onStartServer: () -> Unit,
    val onStopServer: () -> Unit,

    val onStartDiscovery: () -> Unit,
    val onStopDiscovery: () -> Unit
)

data class PeerUi(
    val id: PeerId,
    val name: String,
    val onClick: () -> Unit
)