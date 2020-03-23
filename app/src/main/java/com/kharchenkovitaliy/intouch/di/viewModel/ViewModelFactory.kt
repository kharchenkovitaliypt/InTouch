package com.kharchenkovitaliy.intouch.di.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider: Provider<out ViewModel> = creators[modelClass]
            ?: creators.entries
                .find { (key, _) -> modelClass.isAssignableFrom(key) }
                ?.value
            ?: throw IllegalArgumentException("Unknown model class $modelClass")

        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}