package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.shared.Result
import kotlinx.coroutines.flow.Flow

interface NsdService {
    suspend fun registerService(service: NsdServiceInfo): Result<NsdServiceInfo, NsdErrorCode>
    suspend fun unregisterService(freshService: NsdServiceInfo): Result<Unit, NsdErrorCode>

    suspend fun startDiscovery(serviceType: NsdServiceType): Result<Flow<ServiceEvent>, NsdErrorCode>
    suspend fun stopDiscovery(serviceType: NsdServiceType): Result<Unit, NsdErrorCode>

    suspend fun resolveService(service: NsdServiceInfo): Result<NsdServiceInfo, NsdErrorCode>
}

sealed class ServiceEvent(open val allServices: List<NsdServiceInfo>) {
    data class Found(
        val service: NsdServiceInfo,
        override val allServices: List<NsdServiceInfo>
    ) : ServiceEvent(allServices)

    data class Lost(
        val service: NsdServiceInfo,
        override val allServices: List<NsdServiceInfo>
    ) : ServiceEvent(allServices)
}


