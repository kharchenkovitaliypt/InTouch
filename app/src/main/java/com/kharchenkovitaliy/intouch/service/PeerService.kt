package com.kharchenkovitaliy.intouch.service

import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.model.Peer
import com.kharchenkovitaliy.intouch.model.PeerId
import com.kharchenkovitaliy.intouch.service.nsd.*
import com.kharchenkovitaliy.intouch.service.server.ServerService
import com.kharchenkovitaliy.intouch.shared.Result
import com.kharchenkovitaliy.intouch.shared.getOrNull
import com.kharchenkovitaliy.intouch.shared.mapError
import com.kharchenkovitaliy.intouch.shared.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.random.Random

private typealias ErrorDescription = String

class PeerService @Inject constructor(
    private val nsdService: NsdService,
    private val serverService: ServerService
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val serviceType = NsdServiceType("_nsdchat._tcp")

    private var deferredRegistrationResult: Deferred<Result<NsdServiceInfo, ErrorDescription>>? = null

    private var onPeersChanged: ((List<Peer>) -> Unit)? = null
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
        val port = startServerSocket()

        val serviceInfo = NsdServiceInfo().also {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            it.serviceName = name
            it.serviceType = serviceType.value
            it.port = port
        }
        return nsdService.registerService(serviceInfo)
            .mapError { it.description }
    }

    private suspend fun startServerSocket(): Int {
        // TODO Implement ServerSocket(0)
        return Random.nextInt(65000)
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
        onPeersChanged: (List<Peer>) -> Unit
    ): Result<Unit, ErrorDescription> {
        val peers = mutableListOf<Peer>()
        this.onPeersChanged = onPeersChanged
        onPeersChanged(peers)

        return nsdService.startDiscovery(
            serviceType = serviceType,
            onServiceFound = { serviceInfo ->
                peers.add(serviceInfo.toPeer())
                onPeersChanged(peers)
            },
            onServiceLost = { serviceInfo ->
                peers -= serviceInfo.toPeer()
                onPeersChanged(peers)
            }
        ).mapError { it.description }
    }

    suspend fun stopDiscovery(): Result<Unit, ErrorDescription> {
        onPeersChanged?.invoke(emptyList())
        onPeersChanged = null

        return nsdService.stopDiscovery(serviceType)
            .mapError { it.description }
    }
}

private fun NsdServiceInfo.toPeer() =
    Peer(id = getPeerId(), name = serviceName)

private fun NsdServiceInfo.getPeerId() =
    PeerId(serviceType + serviceName)
