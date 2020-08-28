package com.vitaliykharchenko.intouch.shared.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

val CoroutineContext.job: Job
    get() = get(Job)!!

fun Job.invokeOnCancellation(block: () -> Unit): DisposableHandle =
    invokeOnCompletion { throwable ->
        (throwable as? CancellationException)
            ?.let { block() }
    }