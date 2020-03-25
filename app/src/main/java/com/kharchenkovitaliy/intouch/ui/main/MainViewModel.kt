package com.kharchenkovitaliy.intouch.ui.main

import android.os.Build
import androidx.lifecycle.*
import com.kharchenkovitaliy.intouch.model.Peer
import com.kharchenkovitaliy.intouch.service.PeerService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val peerService: PeerService
) : ViewModel() {
    val serverServiceLiveData: LiveData<String> = liveData {
        peerService.serverServiceFlow
            .map { it?.serviceName ?: "????" }
            .collect(::emit)
    }
    val peersLiveData = MutableLiveData<List<Peer>>()

    fun register() {
        viewModelScope.launch {
            peerService.startServer(Build.DEVICE)
        }
    }

    fun unregister() {
        viewModelScope.launch {
            peerService.unregister()
        }
    }

    fun startDiscovery() {
        viewModelScope.launch {
            peerService.startDiscover(
                onPeersChanged = { peers ->
                    peersLiveData.value = peers
                }
            )
        }
    }

    fun stopDiscovery() {
        viewModelScope.launch {
            peerService.stopDiscovery()
        }
    }
}