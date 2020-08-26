package com.vitaliykharchenko.intouch.service.nsd

import android.net.nsd.NsdServiceInfo

// TODO Optimize comparison
fun NsdServiceInfo.isSame(service: NsdServiceInfo): Boolean =
    serviceType.trimDots() == service.serviceType.trimDots()
            && serviceName.trimDots() == service.serviceName.trimDots()

private fun String.trimDots(): String = trim('.')