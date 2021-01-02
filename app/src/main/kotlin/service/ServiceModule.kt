package com.vitaliykharchenko.intouch.service

import com.vitaliykharchenko.intouch.di.AppScope
import com.vitaliykharchenko.intouch.service.nearby.NearbyService
import dagger.Binds
import dagger.Module

@Module interface ServiceModule {

//    @AppScope
//    @Binds fun serverService(service: ServerServiceImpl): ServerService

    @AppScope
    @Binds fun discoveryService(service: NearbyService): DiscoveryService
}