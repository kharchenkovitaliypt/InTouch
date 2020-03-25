package com.kharchenkovitaliy.intouch.service

import com.kharchenkovitaliy.intouch.di.AppScope
import com.kharchenkovitaliy.intouch.service.nsd.NsdModule
import com.kharchenkovitaliy.intouch.service.server.ServerService
import com.kharchenkovitaliy.intouch.service.server.ServerServiceImpl
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
}