package com.kharchenkovitaliy.intouch

import android.content.Context
import com.kharchenkovitaliy.intouch.di.AppComponent
import com.kharchenkovitaliy.intouch.di.AppModule
import com.kharchenkovitaliy.intouch.di.DaggerAppComponent
import com.kharchenkovitaliy.intouch.shared.coroutines.job
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class App : DaggerApplication() {

    lateinit var component : AppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = component

    override fun onCreate() {
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        super.onCreate()
    }

    companion object {
        fun getComponent(ctx: Context) = get(ctx).component

        fun get(ctx: Context): App = ctx.applicationContext as App
    }
}

fun main() = runBlocking {
    println("main() job: ${kotlin.coroutines.coroutineContext.job}")

    coroutineScope {
        println("coroutineScope job: ${kotlin.coroutines.coroutineContext.job}")
        try {
            doSomething()
        } catch(e: Throwable) {
            println("tryCatch: $e")
        }
    }
}

suspend fun doSomething(): Unit =
    withContext(Dispatchers.Default) {
        println("doSomething() job: ${coroutineContext.job}")
        delay(10)
        throw ArithmeticException()
    }