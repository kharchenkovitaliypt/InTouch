package com.kharchenkovitaliy.intouch.service

import com.kharchenkovitaliy.intouch.service.nsd.NsdModule
import com.kharchenkovitaliy.intouch.service.server.ServerService
import com.kharchenkovitaliy.intouch.service.server.ServerServiceImpl
import dagger.Module
import dagger.Provides

@Module(includes = [
    NsdModule::class
])
object ServiceModule {
    @Provides fun serverService(service: ServerServiceImpl): ServerService = service
}