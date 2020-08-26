package com.vitaliykharchenko.intouch.ui

import com.vitaliykharchenko.intouch.ui.main.MainModule
import dagger.Module

@Module(includes = [
    MainModule::class
])
interface UiModule