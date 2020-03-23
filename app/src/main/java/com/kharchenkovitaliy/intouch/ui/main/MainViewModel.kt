package com.kharchenkovitaliy.intouch.ui.main

import android.os.Build
import androidx.lifecycle.*
import com.kharchenkovitaliy.intouch.service.ConnectionService
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val connectionService: ConnectionService
) : ViewModel() {
    val myService = MutableLiveData<String>()
    val otherServices = MutableLiveData<List<String>>()

    fun register() {
        viewModelScope.launch {
            connectionService.register(Build.DEVICE) { service ->
                myService.value = service?.serviceName ?: "????"
            }
        }
    }

    fun unregister() {
        viewModelScope.launch {
            connectionService.unregister()
        }
    }

    fun startDiscovery() {
        viewModelScope.launch {
            connectionService.startDiscover(
                onServicesChanged = { services ->
                    otherServices.value = services.map { it.serviceName }
                }
            )
        }
    }

    fun stopDiscovery() {
        viewModelScope.launch {
            connectionService.stopDiscovery()
        }
    }
}