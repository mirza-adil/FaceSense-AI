package com.hobotech.facesenseai.greeting.di

import com.hobotech.facesenseai.greeting.presentation.FaceSenseViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

/** FaceSenseViewModel is created with parametersOf(scope): viewModelScope on Android, MainScope on iOS. */
val faceSenseModule = module {
    factory { (scope: CoroutineScope) ->
        FaceSenseViewModel(get(), scope)
    }
}
