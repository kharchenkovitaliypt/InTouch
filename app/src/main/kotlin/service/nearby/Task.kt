package com.vitaliykharchenko.intouch.service.nearby

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await as kAwait

suspend fun <T> Task<T>.await(): Result<T, Exception> =
    try {
        Ok(this.kAwait())
    } catch (e: Exception) {
        Err(e)
    }