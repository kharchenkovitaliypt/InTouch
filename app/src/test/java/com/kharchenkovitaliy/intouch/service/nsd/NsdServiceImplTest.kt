package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo
import com.kharchenkovitaliy.intouch.shared.coroutines.dispatcher
import com.kharchenkovitaliy.intouch.shared.getOrThrow
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class NsdServiceImplTest {

    @Test fun registerAndUnregisterService() = runBlocking<Unit> {
        val info = NsdServiceInfo()
        val freshInfo = NsdServiceInfo()

        val nsdManager = mock<NsdManager>()
        whenever(nsdManager.registerService(eq(info), any())).doAnswer {
            (it.arguments[1] as RegistrationListener).onServiceRegistered(freshInfo)
        }
        whenever(nsdManager.unregisterService(any())).doAnswer {
            (it.arguments[0] as RegistrationListener).onServiceUnregistered(freshInfo)
        }

        val nsdService = NsdServiceImpl(nsdManager, coroutineContext.dispatcher)

        val actualFreshInfo = nsdService.registerService(info).getOrThrow()
        assertEquals(freshInfo, actualFreshInfo)

        val listenerCaptor = argumentCaptor<RegistrationListener>()
        verify(nsdManager).registerService(eq(info), listenerCaptor.capture())

        nsdService.unregisterService(freshInfo).getOrThrow()
        verify(nsdManager).unregisterService(eq(listenerCaptor.firstValue))
    }
}