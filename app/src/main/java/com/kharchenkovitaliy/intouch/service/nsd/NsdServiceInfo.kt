package com.kharchenkovitaliy.intouch.service.nsd

import android.net.nsd.NsdServiceInfo

fun NsdServiceInfo.isSame(service: NsdServiceInfo): Boolean =
    serviceType == service.serviceType
            && serviceName == service.serviceName