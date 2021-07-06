package com.vitaliykharchenko.intouch.service.nearby

import android.Manifest
import android.content.Context
import android.util.Log
import com.github.michaelbull.result.onFailure
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.vitaliykharchenko.intouch.di.AppScope
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.Permission
import com.vitaliykharchenko.intouch.service.PermissionService
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import com.vitaliykharchenko.intouch.shared.collection.removeFirst
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val SERVICE_ID = "com.vitaliykharchenko.intouch"

private val requiredPermissions: List<Permission> = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

sealed class PeersState {
    data class Waiting(val needPermissions: List<Permission>) : PeersState()
    data class Data(val peers: List<Peer>) : PeersState()
    class Error(val desc: ErrorDescription) : PeersState()
}

@AppScope
class NearbyService @Inject constructor(
    private val context: Context,
    private val permissionService: PermissionService,
) {
    private val client: ConnectionsClient by lazy {
        Nearby.getConnectionsClient(context)
    }
    private val strategy = Strategy.P2P_POINT_TO_POINT

    fun advertise() {
        val options = AdvertisingOptions.Builder()
            .setStrategy(strategy)
            .build()
        val callback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                Log.d("TAG", "onConnectionInitiated: accepting connection")

                client.acceptConnection(endpointId, object : PayloadCallback() {
                    override fun onPayloadReceived(endpointId: String, payload: Payload) {
                    }

                    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                    }
                })
//                friendCodeName = connectionInfo.endpointName
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {

                // you can do if(result.status.isSuccess){}else{} or check the statusCode with "when"

                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // We're connected! Can now start sending and receiving data.

                        // Once you have successfully connected to your friends' devices, you can leave
                        // discovery mode so you can stop discovering other devices
                        client.stopDiscovery();
                        // if you were advertising, you can stop as well
                        client.stopAdvertising()

//                        friendEndpointId = endpointId
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        // The connection was rejected by one or both sides.
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        // The connection broke before it was able to be accepted.
                    }
                    else -> {
                        // Unknown status code
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                Log.d("TAG", "onDisconnected: from friend")
                // perform necessary clean up
            }
        }
        client.startAdvertising("Man", SERVICE_ID, callback, options)
    }

    fun discover(): Flow<PeersState> {

        return callbackFlow {
            trySend(PeersState.Waiting(requiredPermissions))
            permissionService.awaitPermissions(requiredPermissions)

            val peers = mutableListOf<Peer>()
            val callback = object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    peers += Peer(endpointId, info.endpointName)
                    trySend(PeersState.Data(peers))
                }
                override fun onEndpointLost(endpointId: String) {
                    peers.removeFirst { it.id == endpointId }
                    trySend(PeersState.Data(peers))
                }
            }
            val options = DiscoveryOptions.Builder()
                .setStrategy(strategy)
                .build()
            client.startDiscovery(SERVICE_ID, callback, options).await()
                .onFailure {
                    send(PeersState.Error(it.toString()))
                }

            awaitClose {
                client.stopDiscovery()
            }
        }.buffer(Channel.UNLIMITED)
    }

}