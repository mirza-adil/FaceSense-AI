package com.hobotech.facesenseai.greeting.di

import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

/** GreetingViewModel is created with parametersOf(scope): viewModelScope on Android, MainScope on iOS. */
val greetingModule = module {
    factory { (scope: CoroutineScope) ->
        GreetingViewModel(get(), scope)
    }
}
