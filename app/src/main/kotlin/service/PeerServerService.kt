package com.vitaliykharchenko.intouch.service

import android.net.nsd.NsdServiceInfo
import com.github.michaelbull.result.Result
import com.vitaliykharchenko.intouch.service.shared.ErrorDescription
import kotlinx.coroutines.flow.Flow

interface PeerServerService {
    val serviceFlow: Flow<NsdServiceInfo?>
    suspend fun start(name: String): Result<Unit, ErrorDescription>
    suspend fun stop()
}