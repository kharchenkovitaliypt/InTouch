package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.shared.Result

inline class NsdErrorCode(val value: Int)

interface NsdService {

    suspend fun registerService(serviceInfo: NsdServiceInfo): Result<NsdServiceInfo, NsdErrorCode>

    suspend fun unregisterService(freshServiceInfo: NsdServiceInfo): Result<Unit, NsdErrorCode>

    suspend fun startDiscovery(
        serviceType: NsdServiceType,
        onServiceFound: (NsdServiceInfo) -> Unit,
        onServiceLost: (NsdServiceInfo) -> Unit
    ): Result<Unit, NsdErrorCode>

    suspend fun stopDiscovery(serviceType: NsdServiceType): Result<Unit, NsdErrorCode>

}


