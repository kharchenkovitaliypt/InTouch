package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.shared.*
import com.kharchenkovitaliy.intouch.shared.collection.copy
import com.kharchenkovitaliy.intouch.shared.collection.removeFirst
import com.kharchenkovitaliy.intouch.shared.coroutines.SerialCoroutineDispatcher
import com.kharchenkovitaliy.intouch.shared.coroutines.StatefulChannelFlow
import com.kharchenkovitaliy.intouch.shared.coroutines.invokeOnCancellation
import com.kharchenkovitaliy.intouch.shared.coroutines.job
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.net.nsd.NsdManager.DiscoveryListener as NsdDiscoveryListener
import android.net.nsd.NsdManager.RegistrationListener as NsdRegistrationListener
import android.net.nsd.NsdManager.ResolveListener as NsdResolveListener

class NsdServiceImpl @Inject constructor(
    private val nsdManager: NsdManager,
    private val dispatcher: CoroutineDispatcher = SerialCoroutineDispatcher()
) : NsdService {
    private val registrationCallbacks = mutableMapOf<NsdServiceInfo, RegistrationCallback>()
    private val discoveryCallbacks = mutableMapOf<NsdServiceType, DiscoveryCallback>()

    override suspend fun registerService(service: NsdServiceInfo): Result<NsdServiceInfo, NsdErrorCode> =
        withContext(dispatcher) {
            val callback = RegistrationCallback()

            coroutineContext.job?.invokeOnCancellation {
                nsdManager.unregisterService(callback)
            }
            nsdManager.registerService(service, callback)

            callback.registerResult.await()
                .onSuccess { freshService ->
                    registrationCallbacks[freshService] = callback
                }
        }

    override suspend fun unregisterService(freshService: NsdServiceInfo): Result<Unit, NsdErrorCode> =
        withContext(dispatcher) {
            registrationCallbacks.remove(freshService)
                ?.let { callback ->
                    nsdManager.unregisterService(callback)
                    callback.unregisterResult.await()
                }
                ?: Result.success()
        }

    override suspend fun startDiscovery(serviceType: NsdServiceType): Result<Flow<ServiceEvent>, NsdErrorCode> =
        withContext(dispatcher) {
            val callback = DiscoveryCallback()
            coroutineContext.job?.invokeOnCancellation {
                nsdManager.stopServiceDiscovery(callback)
            }
            nsdManager.discoverServices(serviceType, callback)

            callback.startDiscoveryResult.await()
                .onSuccess {
                    discoveryCallbacks[serviceType] = callback
                }
                .map { flow -> flow.resolveFoundService() }
        }

    private fun Flow<ServiceEvent>.resolveFoundService(): Flow<ServiceEvent> =
        scan(null as ServiceEvent?) { acc: ServiceEvent?, event: ServiceEvent ->
            if (acc == null) {
                event.resolveAllServices()
            } else {
                when (event) {
                    is ServiceEvent.Found -> event.resolveService() ?: acc // Ignore unresolved services
                    is ServiceEvent.Lost -> event
                }
            }
        }.filterNotNull()

    private suspend fun ServiceEvent.resolveAllServices(): ServiceEvent {
        val allResolvedServices = allServices.mapNotNull { service ->
            resolveService(service).getOrNull()
        }
        val resolvedService = allResolvedServices.first(service::isSame)
        return when (this) {
            is ServiceEvent.Found -> ServiceEvent.Found(resolvedService, allResolvedServices)
            is ServiceEvent.Lost -> ServiceEvent.Lost(resolvedService, allResolvedServices)
        }
    }

    private suspend fun ServiceEvent.Found.resolveService(): ServiceEvent.Found? {
        val resolvedService = resolveService(service)
            .getOrElse { return null }
        val allResolvedServices = allServices.copy {
            service::isSame replaceOn resolvedService
        }
        return ServiceEvent.Found(resolvedService, allResolvedServices)
    }

    override suspend fun stopDiscovery(serviceType: NsdServiceType): Result<Unit, NsdErrorCode> =
        withContext(dispatcher) {
            discoveryCallbacks.remove(serviceType)
                ?.let { callback ->
                    nsdManager.stopServiceDiscovery(callback)
                    callback.stopDiscoveryResult.await()
                }
                ?: Result.success()
        }

    override suspend fun resolveService(service: NsdServiceInfo): Result<NsdServiceInfo, NsdErrorCode> =
        withContext(dispatcher) {
            val callback = ResolveCallback()
            nsdManager.resolveService(service, callback)
            callback.resolveResult.await()
        }
}

private class RegistrationCallback : NsdRegistrationListener {
    val registerResult = CompletableDeferred<Result<NsdServiceInfo, NsdErrorCode>>()
    val unregisterResult = CompletableDeferred<Result<Unit, NsdErrorCode>>()

    override fun onServiceRegistered(service: NsdServiceInfo) {
        registerResult.complete(Result.success(service))
    }

    override fun onRegistrationFailed(service: NsdServiceInfo, errorCode: Int) {
        registerResult.complete(
            Result.failure(NsdErrorCode(errorCode)))
    }

    override fun onServiceUnregistered(service: NsdServiceInfo) {
        unregisterResult.complete(Result.success())
    }

    override fun onUnregistrationFailed(service: NsdServiceInfo, errorCode: Int) {
        unregisterResult.complete(
            Result.failure(NsdErrorCode(errorCode)))
    }
}

private class DiscoveryCallback : NsdDiscoveryListener {
    val startDiscoveryResult = CompletableDeferred<Result<Flow<ServiceEvent>, NsdErrorCode>>()
    var stopDiscoveryResult = CompletableDeferred<Result<Unit, NsdErrorCode>>()

    private val servicesRef = AtomicRef(emptyList<NsdServiceInfo>())
    private val serviceEventFlow = StatefulChannelFlow<ServiceEvent>()

    // Called as soon as service discovery begins.
    override fun onDiscoveryStarted(regType: String) {
        startDiscoveryResult.complete(
            Result.success(serviceEventFlow))
    }

    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        startDiscoveryResult.complete(
            Result.failure(NsdErrorCode(errorCode)))
    }

    override fun onServiceFound(service: NsdServiceInfo) {
        val services = servicesRef.updateAndGetCompat { list ->
            list + service
        }
        serviceEventFlow.offer(ServiceEvent.Found(service, services))
    }

    override fun onServiceLost(service: NsdServiceInfo) {
        val services = servicesRef.updateAndGetCompat { list ->
            list.removeFirst(service::isSame)
        }
        serviceEventFlow.offer(ServiceEvent.Lost(service, services))
    }

    override fun onDiscoveryStopped(serviceType: String) {
        stopDiscoveryResult.complete(Result.success())
    }

    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        stopDiscoveryResult.complete(
            Result.failure(NsdErrorCode(errorCode)))
    }
}

private class ResolveCallback : NsdResolveListener {
    val resolveResult = CompletableDeferred<Result<NsdServiceInfo, NsdErrorCode>>()

    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
        resolveResult.complete(Result.success(serviceInfo))
    }

    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        resolveResult.complete(Result.failure(NsdErrorCode(errorCode)))
    }
}