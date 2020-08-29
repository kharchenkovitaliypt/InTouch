package com.vitaliykharchenko.intouch.service.nearby

import android.Manifest
import android.content.Context
import com.github.michaelbull.result.onFailure
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import com.vitaliykharchenko.intouch.model.Peer
import com.vitaliykharchenko.intouch.service.PeerDiscoveryService
import com.vitaliykharchenko.intouch.service.PeersState
import com.vitaliykharchenko.intouch.service.Permission
import com.vitaliykharchenko.intouch.shared.collection.removeFirst
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val SERVICE_ID = "com.vitaliykharchenko.intouch"

class NearbyService(val context: Context) : PeerDiscoveryService {

    override val needPermissions: List<Permission> = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override suspend fun getPeersStateFlow(): Flow<PeersState> {
        val options = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()
        val client = Nearby.getConnectionsClient(context)

        return callbackFlow {
            offer(PeersState.Waiting)

            val peers = mutableListOf<Peer>()
            val callback = object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    peers += Peer(endpointId, info.endpointName)
                    offer(PeersState.Data(peers))
                }
                override fun onEndpointLost(endpointId: String) {
                    peers.removeFirst { it.id == endpointId }
                    offer(PeersState.Data(peers))
                }
            }
            client.startDiscovery(SERVICE_ID, callback, options).await()
                .onFailure { offer(PeersState.Error(it.toString())) }

            awaitClose {
                client.stopDiscovery()
            }
        }
    }

}