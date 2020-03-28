package com.kharchenkovitaliy.intouch.shared

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

fun SerialCoroutineDispatcher(): ExecutorCoroutineDispatcher =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()