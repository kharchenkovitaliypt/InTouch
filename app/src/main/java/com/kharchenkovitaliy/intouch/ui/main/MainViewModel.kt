package com.kharchenkovitaliy.intouch.ui.main

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.kharchenkovitaliy.intouch.model.Peer
import com.kharchenkovitaliy.intouch.service.PeerDiscoveryService
import com.kharchenkovitaliy.intouch.service.PeerServerService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val peerServerService: PeerServerService,
    private val peerDiscoveryService: PeerDiscoveryService
) : ViewModel() {
    val serverFlow: Flow<String> = flow {
        peerServerService.serviceFlow
            .map { it?.serviceName ?: "????" }
            .collect(::emit)
    }
    val peersLiveData: LiveData<List<Peer>> = liveData {
        peerDiscoveryService.peersFlow
            .collect(::emit)
    }

    fun startServer() {
        viewModelScope.launch {
            peerServerService.start(Build.DEVICE)
        }
    }

    fun stopServer() {
        viewModelScope.launch {
            peerServerService.stop()
        }
    }

    fun startDiscovery() {
        viewModelScope.launch {
            peerDiscoveryService.start()
        }
    }

    fun stopDiscovery() {
        viewModelScope.launch {
            peerDiscoveryService.stop()
        }
    }
}