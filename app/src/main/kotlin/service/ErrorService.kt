package com.vitaliykharchenko.intouch.service

import java.lang.Exception

class ErrorService {

    fun getDescription(exception: Exception): String {
        return exception.message ?: exception.toString()
    }
}