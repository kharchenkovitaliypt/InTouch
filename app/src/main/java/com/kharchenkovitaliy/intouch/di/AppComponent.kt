package com.kharchenkovitaliy.intouch.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.kharchenkovitaliy.intouch.App
import com.kharchenkovitaliy.intouch.di.viewModel.ViewModelFactory
import com.kharchenkovitaliy.intouch.service.ServiceModule
import com.kharchenkovitaliy.intouch.ui.UiModule
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@AppScope
@Component(modules = [
    AppModule::class,
    ServiceModule::class,
    UiModule::class
])
interface AppComponent  : AndroidInjector<App> {
    fun context(): Context
}

@Module(includes = [AndroidInjectionModule::class])
class AppModule(private val app: App) {

    @Provides fun context(): Context = app

    @Provides fun viewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory = factory
}