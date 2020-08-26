package com.vitaliykharchenko.intouch.service.wifidirect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.core.content.getSystemService

class WifiDirectService(private val context: Context) {

    private val manager = context.getSystemService<WifiP2pManager>()!!

    private val receiver = WifiDirectBroadcastReceiver()
    private var channel: WifiP2pManager.Channel? = null

    fun start() {
        if (channel != null) return
        channel = manager.initialize(context, Looper.getMainLooper(), null)
        context.registerReceiver(receiver, WifiDirectIntentFilter())
    }

    fun stop() {
        channel ?: return
        context.unregisterReceiver(receiver)
        channel?.close()
        channel = null
    }
}

private class WifiDirectBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("WifiDirectBroadcastReceiver.onReceive() intent: $intent, bundle: ${intent.extras}")

        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                val isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED

                println("WifiDirectBroadcastReceiver.onReceive() isEnabled: $isWifiP2pEnabled")
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {

                // The peer list has changed! We should probably do something about
                // that.

            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

                // Connection state changed! We should probably do something about
                // that.

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device: WifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)!!
//                (activity.supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment)
//                    .apply {
//                        updateThisDevice()
//                    }
            }
        }
    }
}

private fun WifiDirectIntentFilter() = IntentFilter().apply {

    // Indicates a change in the Wi-Fi P2P status.
    addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

    // Indicates a change in the list of available peers.
    addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

    // Indicates the state of Wi-Fi P2P connectivity has changed.
    addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

    // Indicates this device's details have changed.
    addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
}