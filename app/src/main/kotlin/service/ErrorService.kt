package com.vitaliykharchenko.intouch.service

import com.vitaliykharchenko.intouch.di.AppScope
import javax.inject.Inject

@AppScope
class ErrorService @Inject constructor() {

    fun getDescription(exception: Exception): String {
        return exception.message ?: exception.toString()
    }
}