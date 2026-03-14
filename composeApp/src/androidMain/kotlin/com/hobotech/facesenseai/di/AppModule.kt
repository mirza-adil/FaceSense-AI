package com.hobotech.facesenseai.di

import androidx.lifecycle.ViewModel
import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * App-level Koin module for Android.
 * Provides GreetingHostViewModel which holds a GreetingViewModel scoped to the ViewModel lifecycle.
 */
val appModule = module {
    viewModel { GreetingHostViewModel() }
}

/**
 * Android ViewModel that provides [GreetingViewModel] with [viewModelScope].
 * Used by composables via koinViewModel<GreetingHostViewModel>().
 */
class GreetingHostViewModel : ViewModel() {
    val greetingViewModel: GreetingViewModel by lazy {
        GlobalContext.get().get { parametersOf(viewModelScope) }
    }
}
