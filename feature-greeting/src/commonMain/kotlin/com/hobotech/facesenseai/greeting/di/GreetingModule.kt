package com.hobotech.facesenseai.greeting.di

import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

/**
 * Koin module for the greeting feature.
 * GreetingViewModel is obtained with parametersOf(scope) where scope is viewModelScope (Android) or MainScope() (iOS).
 */
val greetingModule = module {
    factory { (scope: CoroutineScope) ->
        GreetingViewModel(get(), scope)
    }
}
