package com.vitaliykharchenko.intouch.service

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.vitaliykharchenko.intouch.di.AppScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

typealias Permission = String

@AppScope
class PermissionService @Inject constructor(
    private val context: Context
) {
    private val permissionsChangedFlow =
        MutableSharedFlow<Unit>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun onPermissionsChanged() {
        permissionsChangedFlow.tryEmit(Unit)
    }

    suspend fun awaitPermissions(permissions: List<Permission>) {
        permissionsChangedFlow
            .first { isPermissionGranted(permissions) }
    }

    private fun isPermissionGranted(permissions: List<Permission>): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
}