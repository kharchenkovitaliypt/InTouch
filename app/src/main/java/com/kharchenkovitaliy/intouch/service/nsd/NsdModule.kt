package com.kharchenkovitaliy.intouch.service.nsd

import android.content.Context
import com.kharchenkovitaliy.intouch.di.AppScope
import dagger.Module
import dagger.Provides

@Module object NsdModule {
    @AppScope
    @Provides fun nsdManager(context: Context): NsdManager = context.nsdManager

    @AppScope
    @Provides fun nsdService(service: NsdServiceImpl): NsdService = service
}