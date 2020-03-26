package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdManager.DiscoveryListener
import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo

class NsdServiceType(val value: String)

interface NsdManager {
    fun registerService(serviceInfo: NsdServiceInfo, listener: RegistrationListener)
    fun unregisterService(listener: RegistrationListener)

    fun discoverServices(serviceType: NsdServiceType, listener: DiscoveryListener)
    fun stopServiceDiscovery(listener: DiscoveryListener)
}
