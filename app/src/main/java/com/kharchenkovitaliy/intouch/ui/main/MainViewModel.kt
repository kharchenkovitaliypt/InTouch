package com.kharchenkovitaliy.intouch.ui.main

import android.os.Build
import androidx.lifecycle.*
import com.kharchenkovitaliy.intouch.model.Peer
import com.kharchenkovitaliy.intouch.service.PeerService
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val peerService: PeerService
) : ViewModel() {
    val myService = MutableLiveData<String>()
    val peersLiveData = MutableLiveData<List<Peer>>()

    fun register() {
        viewModelScope.launch {
            peerService.register(Build.DEVICE) { service ->
                myService.value = service?.serviceName ?: "????"
            }
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