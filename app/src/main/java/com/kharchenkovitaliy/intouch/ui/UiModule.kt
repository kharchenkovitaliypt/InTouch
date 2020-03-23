package com.kharchenkovitaliy.intouch.ui

import com.kharchenkovitaliy.intouch.ui.main.MainModule
import dagger.Module

@Module(includes = [
    MainModule::class
])
interface UiModule