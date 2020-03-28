package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdManager.*
import android.net.nsd.NsdServiceInfo

class NsdServiceType(val value: String)

interface NsdManager {
    fun registerService(service: NsdServiceInfo, listener: RegistrationListener)
    fun unregisterService(listener: RegistrationListener)

    fun discoverServices(serviceType: NsdServiceType, listener: DiscoveryListener)
    fun stopServiceDiscovery(listener: DiscoveryListener)

    fun resolveService(service: NsdServiceInfo, listener: ResolveListener)
}
