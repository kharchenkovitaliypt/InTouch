package com.vitaliykharchenko.intouch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.DiscoveryService
import com.vitaliykharchenko.intouch.service.PeersState
import com.vitaliykharchenko.intouch.service.server.ServerServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val serverService: ServerServiceImpl,
    discoveryService: DiscoveryService
) : ViewModel() {

    private val isPeersDiscoveryEnabledFlow = MutableStateFlow(false)

    private val _uiFlow = MutableStateFlow(
        MainUi(
            serverName = "",
            peersState = PeersUiState.Idle,
            onStartServer = {
                viewModelScope.launch {
                    serverService.start()
                }
            },
            onStopServer = {
                viewModelScope.launch {
                    serverService.stop()
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
//        combine(
//            serverService.serviceFlow.map { it?.serviceName ?: "????" },
//            isPeersDiscoveryEnabledFlow.flatMapLatest { enabled ->
//                if (enabled) {
//                    discoveryService.getPeersStateFlow()
//                        .map { it.asPeersUiState() }
//                } else {
//                    flowOf(PeersUiState.Idle)
//                }
//            }
//        ) { serviceName, peersState ->
//            _uiFlow.value = _uiFlow.value.copy(
//                serverName = serviceName,
//                peersState = peersState
//            )
//        }.launchIn(viewModelScope)
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