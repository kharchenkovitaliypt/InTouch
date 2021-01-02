package com.vitaliykharchenko.intouch.di

import android.content.Context
import dagger.Component

@ActivityScope
@Component(
    dependencies = [
        AppComponent::class
    ],
    modules = [
//        ActivityModule::class
    ]
)
interface ActivityComponent {
    fun context(): Context
}