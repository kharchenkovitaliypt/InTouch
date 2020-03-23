package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.kharchenkovitaliy.intouch.shared.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow



typealias NsdDiscoverServiceResult = Result<Flow<DiscoveryEvent>, NsdErrorCode>

sealed class DiscoveryEvent {
    data class ServiceFound(val info: NsdServiceInfo) : DiscoveryEvent()
    data class ServiceLost(val info: NsdServiceInfo) : DiscoveryEvent()
}






