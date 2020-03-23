package com.kharchenkovitaliy.intouch.ui.main

import androidx.lifecycle.ViewModel
import com.kharchenkovitaliy.intouch.di.viewModel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface MainModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    fun contributesInjector(): MainActivity
}

@Module private interface ViewModelModule {
    @Binds @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun viewModel(viewModel: MainViewModel): ViewModel
}