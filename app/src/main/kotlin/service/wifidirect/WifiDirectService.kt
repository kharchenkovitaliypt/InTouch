package com.vitaliykharchenko.intouch.service.wifidirect

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.*
import android.net.wifi.p2p.WifiP2pManager.Channel as WifiChannel
import android.os.Looper
import androidx.core.content.getSystemService
import com.github.michaelbull.result.*
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.model.PeerId
import com.vitaliykharchenko.intouch.service.PeerDiscoveryService
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WifiDirectService(private val context: Context) : PeerDiscoveryService {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val manager = context.getSystemService<WifiP2pManager>()!!

    val isEnabledFlow: Flow<Boolean> = context.broadcastFlow(
        WIFI_P2P_STATE_CHANGED_ACTION to { intent ->
            intent.getIntExtra(EXTRA_WIFI_STATE, -1) == WIFI_P2P_STATE_ENABLED
        }
    ).onEach {
        println("WifiDirectBroadcastReceiver.onReceive() isEnabled: $it")
    }

    private val channelFlow = MutableStateFlow<WifiChannel?>(null)

    override val peersFlow: Flow<List<Peer>> =
        channelFlow.flatMapLatest { channel ->
            channel?.let { getPeersFlow(channel) } ?: flowOf(emptyList())
        }

    private fun getPeersFlow(channel: WifiChannel): Flow<List<Peer>> =
        context.broadcastFlow(WIFI_P2P_PEERS_CHANGED_ACTION)
            .onEach { println("WifiDirectBroadcastReceiver.requestPeers1() $it") }
            .mapLatest { manager.requestPeers(channel) }
            .onEach { println("WifiDirectBroadcastReceiver.requestPeers2() $it") }
            .map { list ->
                list.deviceList.map { Peer(PeerId(it.deviceAddress), it.deviceName) }
            }
            .onEach { println("WifiDirectBroadcastReceiver.requestPeers3() $it") }

    override suspend fun start(): Result<Unit, ErrorDescription> {
        if (channelFlow.value != null) {
            return Err("Already start")
        }
        val channel = manager.initialize(context, Looper.getMainLooper()) {
            println("WifiDirectBroadcastReceiver channel.onDisconnected()")
        }

        val result = manager.discoverPeers(channel)
        println("WifiDirectBroadcastReceiver.discoverPeers() $result")

        result.onFailure { reason ->
            return Err(toErrorDescription(reason))
        }

        this.channelFlow.value = channel

        return Ok(Unit)
    }

    override suspend fun stop(): Result<Unit, ErrorDescription> {
        channelFlow.value ?: return Err("Already stopped")
        channelFlow.value?.close()
        channelFlow.value = null
        coroutineScope.coroutineContext.cancelChildren()
        return Ok(Unit)
    }
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
    this::discoverPeers.await(channel)

@SuppressLint("MissingPermission")
suspend fun WifiP2pManager.requestPeers(channel: WifiChannel): WifiP2pDeviceList =
    suspendCoroutine { cont ->
        this.requestPeers(channel) {
            cont.resume(it)
        }
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

private suspend fun ((WifiChannel, ActionListener) -> Unit).await(channel: WifiChannel): Result<Unit, FailureReason> =
    suspendCoroutine { cont ->
        val listener = object : ActionListener {
            override fun onSuccess() { cont.resume(Ok(Unit)) }
            override fun onFailure(reason: Int) { cont.resume(Err(reason)) }
        }
        this@await.invoke(channel, listener)
    }

