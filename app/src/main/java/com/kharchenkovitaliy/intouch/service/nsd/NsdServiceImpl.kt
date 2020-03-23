package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdServiceInfo
import android.os.Handler
import android.os.Looper
import com.kharchenkovitaliy.intouch.shared.Result
import com.kharchenkovitaliy.intouch.shared.ThreadChecker
import com.kharchenkovitaliy.intouch.shared.onSuccess
import com.kharchenkovitaliy.intouch.shared.success
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.net.nsd.NsdManager as NsdManagerImpl
import android.net.nsd.NsdManager.RegistrationListener as NsdRegistrationListener
import android.net.nsd.NsdManager.DiscoveryListener as NsdDiscoveryListener
import android.net.nsd.NsdManager.ResolveListener as NsdResolveListener

class NsdServiceImpl(
    private val nsdManager: NsdManager
) : NsdService {
    private val threadChecker = ThreadChecker()

    private val serviceListeners = mutableMapOf<NsdServiceInfo, RegistrationListener>()
    private val discoveryListeners = mutableMapOf<NsdServiceType, DiscoveryListener>()

    override suspend fun registerService(serviceInfo: NsdServiceInfo): Result<NsdServiceInfo, NsdErrorCode> =
        suspendCancellableCoroutine { cont ->
            threadChecker.check()
            val listener = RegistrationListener()
            listener.onRegistrationResult = { result ->
                threadChecker.check()
                result.onSuccess { freshInfo ->
                    serviceListeners[freshInfo] = listener
                }
                cont.resume(result)
            }
            cont.invokeOnCancellation {
                nsdManager.unregisterService(listener)
            }
            nsdManager.registerService(serviceInfo, listener)
        }

    override suspend fun unregisterService(freshServiceInfo: NsdServiceInfo): Result<Unit, NsdErrorCode> =
        suspendCancellableCoroutine { cont ->
            threadChecker.check()
            val listener = serviceListeners.remove(freshServiceInfo) ?: run {
                cont.resume(Result.success())
                return@suspendCancellableCoroutine
            }
            listener.onUnregistrationResult = cont::resume
            nsdManager.unregisterService(listener)
        }

    override suspend fun startDiscovery(
        serviceType: NsdServiceType,
        onServiceFound: (NsdServiceInfo) -> Unit,
        onServiceLost: (NsdServiceInfo) -> Unit
    ): Result<Unit, NsdErrorCode> =
        suspendCancellableCoroutine { cont ->
            threadChecker.check()
            val listener = DiscoveryListener(
                onServiceFound = onServiceFound,
                onServiceLost = onServiceLost
            )
            listener.onStartDiscoveryResult = { result ->
                cont.resume(result)
            }
            cont.invokeOnCancellation {
                nsdManager.stopServiceDiscovery(listener)
            }
            nsdManager.discoverServices(serviceType, listener)
        }

    override suspend fun stopDiscovery(serviceType: NsdServiceType): Result<Unit, NsdErrorCode> =
        suspendCancellableCoroutine { cont ->
            threadChecker.check()
            val listener = discoveryListeners.remove(serviceType) ?: run {
                cont.resume(Result.success())
                return@suspendCancellableCoroutine
            }
            listener.onStopDiscoveryResult = cont::resume
            nsdManager.stopServiceDiscovery(listener)
        }
}

val NsdErrorCode.description: String
    get() = when (value) {
        NsdManagerImpl.FAILURE_INTERNAL_ERROR -> "Internal error"
        NsdManagerImpl.FAILURE_ALREADY_ACTIVE -> "The operation is already active"
        NsdManagerImpl.FAILURE_MAX_LIMIT -> "The maximum outstanding requests from the applications have reached"
        else -> "Unknown NSD error: $value"
    }

private typealias NsdOnRegistrationResult = (Result<NsdServiceInfo, NsdErrorCode>) -> Unit
private typealias NsdOnUnregistrationResult = (Result<Unit, NsdErrorCode>) -> Unit

private class RegistrationListener : NsdRegistrationListener {
    private val uiHandler = Handler(Looper.getMainLooper())

    var onRegistrationResult: NsdOnRegistrationResult? = null
    var onUnregistrationResult: NsdOnUnregistrationResult? = null

    override fun onServiceRegistered(info: NsdServiceInfo) {
        uiHandler.post {
            onRegistrationResult!!(Result.success(info))
        }
    }

    override fun onRegistrationFailed(info: NsdServiceInfo, errorCode: Int) {
        uiHandler.post {
            onRegistrationResult!!(Result.failure(NsdErrorCode(errorCode)))
        }
    }

    override fun onServiceUnregistered(info: NsdServiceInfo) {
        uiHandler.post {
            onUnregistrationResult!!(Result.success())
        }
    }

    override fun onUnregistrationFailed(info: NsdServiceInfo, errorCode: Int) {
        uiHandler.post {
            onUnregistrationResult!!(Result.failure(NsdErrorCode(errorCode)))
        }
    }
}

private class DiscoveryListener(
    private val onServiceFound: (NsdServiceInfo) -> Unit,
    private val onServiceLost: (NsdServiceInfo) -> Unit
) : NsdDiscoveryListener {
    private val uiHandler = Handler(Looper.getMainLooper())

    var onStartDiscoveryResult: ((Result<Unit, NsdErrorCode>) -> Unit)? = null
    var onStopDiscoveryResult: ((Result<Unit, NsdErrorCode>) -> Unit)? = null

    // Called as soon as service discovery begins.
    override fun onDiscoveryStarted(regType: String) {
        uiHandler.post {
            onStartDiscoveryResult!!(Result.success())
        }
    }

    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        uiHandler.post {
            onStartDiscoveryResult!!(Result.failure(NsdErrorCode(errorCode)))
        }
    }

    override fun onServiceFound(serviceInfo: NsdServiceInfo) {
        uiHandler.post {
            onServiceFound.invoke(serviceInfo)
        }
    }

    override fun onServiceLost(serviceInfo: NsdServiceInfo) {
        uiHandler.post {
            onServiceLost.invoke(serviceInfo)
        }
    }

    override fun onDiscoveryStopped(serviceType: String) {
        uiHandler.post {
            onStopDiscoveryResult!!(Result.success())
        }
    }

    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        uiHandler.post {
            onStopDiscoveryResult!!(Result.failure(NsdErrorCode(errorCode)))
        }
    }
}

private class ResolveListener(
    private val onResolveResult: (Result<NsdServiceInfo, NsdErrorCode>) -> Unit
) : NsdResolveListener {
    private val uiHandler = Handler(Looper.getMainLooper())

    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
        uiHandler.post {
            onResolveResult(Result.success(serviceInfo))
        }
    }

    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        uiHandler.post {
            onResolveResult(Result.failure(NsdErrorCode(errorCode)))
        }
    }
}