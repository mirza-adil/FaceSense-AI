package com.hobotech.facesenseai.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hobotech.facesenseai.greeting.presentation.FaceSenseViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/** Android app Koin: FaceSenseHostViewModel provides FaceSenseViewModel with viewModelScope. */
val appModule = module {
    viewModel { FaceSenseHostViewModel() }
}

class FaceSenseHostViewModel : ViewModel() {
    val faceSenseViewModel: FaceSenseViewModel by lazy {
        GlobalContext.get().get { parametersOf(viewModelScope) }
    }
}
