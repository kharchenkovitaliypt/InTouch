package com.vitaliykharchenko.intouch.service

import android.content.Context
import com.vitaliykharchenko.intouch.di.AppScope
import com.vitaliykharchenko.intouch.service.nearby.NearbyService
import com.vitaliykharchenko.intouch.service.nsd.NsdModule
import com.vitaliykharchenko.intouch.service.nsd.NsdPeerServerService
import com.vitaliykharchenko.intouch.service.server.ServerService
import com.vitaliykharchenko.intouch.service.server.ServerServiceImpl
import com.vitaliykharchenko.intouch.service.wifidirect.WifiDirectService
import dagger.Module
import dagger.Provides

@Module(includes = [
    NsdModule::class
])
object ServiceModule {
    @AppScope
    @Provides fun serverService(service: ServerServiceImpl): ServerService = service

    @AppScope
    @Provides fun errorService(): ErrorService = ErrorService()

    @AppScope
    @Provides fun nsdPeerServerService(service: NsdPeerServerService): PeerServerService = service

    @AppScope
    @Provides fun peerDiscoveryService(service: NearbyService): PeerDiscoveryService = service

    @AppScope
    @Provides fun wifiDirectService(context: Context) = WifiDirectService(context)

    @AppScope
    @Provides fun nearbyService(context: Context) = NearbyService(context)
}