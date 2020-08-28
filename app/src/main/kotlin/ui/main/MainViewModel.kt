package com.vitaliykharchenko.intouch.ui.main

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.PeerDiscoveryService
import com.vitaliykharchenko.intouch.service.PeerServerService
import com.vitaliykharchenko.intouch.service.PeersState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val peerServerService: PeerServerService,
    peerDiscoveryService: PeerDiscoveryService
) : ViewModel() {

    private val isPeersDiscoveryEnabledFlow = MutableStateFlow(false)

    private val _uiFlow = MutableStateFlow(
        MainUi(
            serverName = "",
            peersState = PeersUiState.Idle,
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
                isPeersDiscoveryEnabledFlow.value = true
            },
            onStopDiscovery = {
                isPeersDiscoveryEnabledFlow.value = false
            }
        )
    )
    val uiFlow: StateFlow<MainUi> = _uiFlow

    init {
        combine(
            peerServerService.serviceFlow.map { it?.serviceName ?: "????" },
            isPeersDiscoveryEnabledFlow.flatMapLatest { enabled ->
                if (enabled) {
                    peerDiscoveryService.getPeersStateFlow()
                        .map { it.asPeersUiState() }
                } else {
                    flowOf(PeersUiState.Idle)
                }
            }
        ) { serviceName, peersState ->
            _uiFlow.value = _uiFlow.value.copy(
                serverName = serviceName,
                peersState = peersState
            )
        }.launchIn(viewModelScope)
    }

    private fun PeersState.asPeersUiState(): PeersUiState =
        when (this) {
            is PeersState.Waiting -> PeersUiState.Waiting
            is PeersState.Data -> PeersUiState.Data(this.peers.map { it.asPeerUi() })
            is PeersState.Error -> PeersUiState.Error(this.desc)
        }

    private fun Peer.asPeerUi() =
        PeerUi(id, name, onClick = {
            // Just do it
        })
}