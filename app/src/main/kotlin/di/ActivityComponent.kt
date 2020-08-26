package com.vitaliykharchenko.intouch.di

import android.content.Context
import dagger.Component

@ActivityScope
@Component(
    dependencies = [AppComponent::class],
    modules = [
//        ActivityModule::class
    ]
)
interface ActivityComponent {
//    fun activity(): Activity
    fun context() : Context
}

//@Module class ActivityModule(private val activity: Activity){
//
//    @Provides fun activity(): Activity = activity
//}