package com.vitaliykharchenko.intouch.service.wifidirect

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.BUSY
import android.net.wifi.p2p.WifiP2pManager.ERROR
import android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_STATE
import android.net.wifi.p2p.WifiP2pManager.NO_SERVICE_REQUESTS
import android.net.wifi.p2p.WifiP2pManager.P2P_UNSUPPORTED
import android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION
import android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION
import android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_ENABLED
import android.os.Looper
import androidx.core.content.getSystemService
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.PeerDiscoveryService
import com.vitaliykharchenko.intouch.service.PeersState
import com.vitaliykharchenko.intouch.service.Permission
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import com.vitaliykharchenko.intouch.shared.coroutines.job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.net.wifi.p2p.WifiP2pManager.Channel as WifiChannel

class WifiDirectService(private val context: Context) : PeerDiscoveryService {

    private val manager = context.getSystemService<WifiP2pManager>()!!

    override val needPermissions: List<Permission>
        get() = TODO("Not yet implemented")

    val isEnabledFlow: Flow<Boolean> = context.broadcastFlow(
        WIFI_P2P_STATE_CHANGED_ACTION to { intent ->
            intent.getIntExtra(EXTRA_WIFI_STATE, -1) == WIFI_P2P_STATE_ENABLED
        }
    ).onEach {
        println("WifiDirectBroadcastReceiver.onReceive() isEnabled: $it")
    }

    override suspend fun getPeersStateFlow(): Flow<PeersState> =
        flow {
            emit(PeersState.Waiting)
            kotlinx.coroutines.delay(1000)
            val job = coroutineContext.job
            val channel = manager.initialize(context, Looper.getMainLooper()) {
                job.cancel()
            }
            manager.discoverPeers(channel)
                .getOrElse { reason ->
                    emit(PeersState.Error(toErrorDescription(reason)))
                    return@flow
                }

            getPeersFlow(channel)
                .map(PeersState::Data)
                .let { emitAll(it) }
        }

    private fun getPeersFlow(channel: WifiChannel): Flow<List<Peer>> =
        context.broadcastFlow(WIFI_P2P_PEERS_CHANGED_ACTION)
            .mapLatest { manager.requestPeers(channel) }
            .map { deviceList -> deviceList.asPeerList() }

    private fun WifiP2pDeviceList.asPeerList(): List<Peer> =
        deviceList.map { Peer(it.deviceAddress, it.deviceName) }
}

//      // Indicates the state of Wi-Fi P2P connectivity has changed.
//      WIFI_P2P_CONNECTION_CHANGED_ACTION to {
//          // Connection state changed!
//          // We should probably do something about that.
//      },
//
//      // Indicates this device's details have changed.
//      WIFI_P2P_THIS_DEVICE_CHANGED_ACTION to { intent ->
//          val device: WifiP2pDevice = intent.getParcelableExtra(EXTRA_WIFI_P2P_DEVICE)!!
//          println("WifiDirectBroadcastReceiver.onReceive() device: $device")
//      }

@SuppressLint("MissingPermission")
suspend fun WifiP2pManager.discoverPeers(channel: WifiChannel): Result<Unit, FailureReason> =
    suspendCoroutine { cont ->
        this.discoverPeers(channel, cont.asActionListener())
    }

@SuppressLint("MissingPermission")
suspend fun WifiP2pManager.requestPeers(channel: WifiChannel): WifiP2pDeviceList =
    suspendCoroutine { cont ->
        this.requestPeers(channel) { cont.resume(it) }
    }

private fun Continuation<Result<Unit, FailureReason>>.asActionListener() =
    object : ActionListener {
        override fun onSuccess() { resume(Ok(Unit)) }
        override fun onFailure(reason: Int) { resume(Err(reason)) }
    }

private typealias FailureReason = Int

private fun toErrorDescription(reason: FailureReason): ErrorDescription =
    when (reason) {
        ERROR -> "Internal error"
        P2P_UNSUPPORTED -> "P2P is unsupported on the device"
        BUSY -> "Framework is busy and unable to service the request"
        NO_SERVICE_REQUESTS -> "No service requests are added"
        else -> "Unknown"
    }

