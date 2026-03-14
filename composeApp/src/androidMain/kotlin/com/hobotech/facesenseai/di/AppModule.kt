package com.hobotech.facesenseai.di

import androidx.lifecycle.ViewModel
import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/** Android app Koin: GreetingHostViewModel provides GreetingViewModel with viewModelScope. */
val appModule = module {
    viewModel { GreetingHostViewModel() }
}

class GreetingHostViewModel : ViewModel() {
    val greetingViewModel: GreetingViewModel by lazy {
        GlobalContext.get().get { parametersOf(viewModelScope) }
    }
}
