package com.kharchenkovitaliy.intouch.service.nsd

import android.content.Context
import android.net.nsd.NsdManager.*
import android.net.nsd.NsdServiceInfo
import android.net.nsd.NsdManager as NsdManagerImpl

internal val Context.nsdManager: NsdManager
    get() = (getSystemService(Context.NSD_SERVICE) as NsdManagerImpl).asNsdManager()

private fun NsdManagerImpl.asNsdManager(): NsdManager =
    object : NsdManager {
        override fun registerService(serviceInfo: NsdServiceInfo, listener: RegistrationListener) {
            this@asNsdManager.registerService(serviceInfo, PROTOCOL_DNS_SD, listener)
        }
        override fun unregisterService(listener: RegistrationListener) {
            this@asNsdManager.unregisterService(listener)
        }

        override fun discoverServices(serviceType: NsdServiceType, listener: DiscoveryListener) {
            this@asNsdManager.discoverServices(serviceType.value, PROTOCOL_DNS_SD, listener)
        }
        override fun stopServiceDiscovery(listener: DiscoveryListener) {
            this@asNsdManager.stopServiceDiscovery(listener)
        }
    }