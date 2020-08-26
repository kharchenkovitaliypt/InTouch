package com.vitaliykharchenko.intouch.service.nsd

import android.content.Context
import com.vitaliykharchenko.intouch.di.AppScope
import dagger.Module
import dagger.Provides

@Module object NsdModule {
    @AppScope
    @Provides fun nsdService(context: Context): CoroutineNsdManager {
        val nsdManager = context.nsdManager
        return CoroutineNsdManagerImpl(nsdManager)
    }

    @AppScope
    @Provides fun nsdServiceType() = NsdServiceType("_nsd_touch._tcp")
}