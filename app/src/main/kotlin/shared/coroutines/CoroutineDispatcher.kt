package com.vitaliykharchenko.intouch.shared.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

val CoroutineContext.dispatcher: CoroutineDispatcher
    get() = get(CoroutineDispatcher)!!

fun SerialCoroutineDispatcher(): ExecutorCoroutineDispatcher =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()