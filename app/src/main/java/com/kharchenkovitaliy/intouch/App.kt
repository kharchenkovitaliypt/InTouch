package com.kharchenkovitaliy.intouch

import android.content.Context
import com.kharchenkovitaliy.intouch.di.AppComponent
import com.kharchenkovitaliy.intouch.di.AppModule
import com.kharchenkovitaliy.intouch.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.runBlocking

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

fun main() = runBlocking<Unit> {

}