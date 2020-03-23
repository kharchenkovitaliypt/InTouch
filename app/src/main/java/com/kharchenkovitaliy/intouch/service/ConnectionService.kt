package com.kharchenkovitaliy.intouch.service

import android.content.Context
import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.service.nsd.*
import com.kharchenkovitaliy.intouch.shared.Result
import com.kharchenkovitaliy.intouch.shared.getOrNull
import com.kharchenkovitaliy.intouch.shared.mapError
import com.kharchenkovitaliy.intouch.shared.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.random.Random

private typealias ErrorDescription = String

class ConnectionService(context: Context) {
    private val nsdService = NsdServiceImpl(context.nsdManager)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val serviceType = NsdServiceType("_nsdchat._tcp")

    private var deferredRegistrationResult: Deferred<Result<NsdServiceInfo, ErrorDescription>>? = null

    private var onOtherServicesChanged: ((List<NsdServiceInfo>) -> Unit)? = null
    private var onMyServiceChanged: ((NsdServiceInfo?) -> Unit)? = null

    suspend fun register(
        name: String,
        onServiceChanged: (NsdServiceInfo?) -> Unit
    ): Result<NsdServiceInfo, ErrorDescription> {
        val serviceInfo = deferredRegistrationResult?.await()?.getOrNull()

        return if (serviceInfo != null) {
            Result.success(serviceInfo)
        } else {
            coroutineScope.async { registerInternal(name) }
                .also { deferredRegistrationResult = it }
                .await()
                .onSuccess { service ->
                    onMyServiceChanged = onServiceChanged
                    onServiceChanged(service)
                }
        }
    }

    private suspend fun registerInternal(name: String): Result<NsdServiceInfo, ErrorDescription> {
        val serviceInfo = NsdServiceInfo().also {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            it.serviceName = name
            it.serviceType = serviceType.value
            it.port = Random.nextInt(65000)
        }
        return nsdService.registerService(serviceInfo)
            .mapError { it.description }
    }

    suspend fun unregister() {
        deferredRegistrationResult?.await()
            ?.onSuccess { serviceInfo ->
                nsdService.unregisterService(serviceInfo)
            }
        deferredRegistrationResult = null

        onMyServiceChanged?.invoke(null)
        onMyServiceChanged = null
    }

    suspend fun startDiscover(
        onServicesChanged: (List<NsdServiceInfo>) -> Unit
    ): Result<Unit, ErrorDescription> {
        val services = mutableListOf<NsdServiceInfo>()
        onOtherServicesChanged = onServicesChanged
        onServicesChanged(services)

        return nsdService.startDiscovery(
            serviceType = serviceType,
            onServiceFound = { serviceInfo ->
                services.add(serviceInfo)
                onServicesChanged(services)
            },
            onServiceLost = { serviceInfo ->
                services.find { it.serviceName == serviceInfo.serviceName }
                    ?.let { services.remove(it) }
                onServicesChanged(services)
            }
        ).mapError { it.description }
    }

    suspend fun stopDiscovery(): Result<Unit, ErrorDescription> {
        onOtherServicesChanged?.invoke(emptyList())
        onOtherServicesChanged = null

        return nsdService.stopDiscovery(serviceType)
            .mapError { it.description }
    }
}
