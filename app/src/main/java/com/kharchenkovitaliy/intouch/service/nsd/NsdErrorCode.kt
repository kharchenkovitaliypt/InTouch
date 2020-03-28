package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdManager

inline class NsdErrorCode(val value: Int)

val NsdErrorCode.description: String
    get() = when (value) {
        NsdManager.FAILURE_INTERNAL_ERROR -> "Internal error"
        NsdManager.FAILURE_ALREADY_ACTIVE -> "The operation is already active"
        NsdManager.FAILURE_MAX_LIMIT -> "The maximum outstanding requests from the applications have reached"
        else -> "Unknown NSD error: $value"
    }