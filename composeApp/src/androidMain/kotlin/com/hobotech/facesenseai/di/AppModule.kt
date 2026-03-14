package com.hobotech.facesenseai.di

import androidx.lifecycle.ViewModel
import com.hobotech.facesenseai.facesense.presentation.FaceSenseViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Android app Koin: FaceSenseHostViewModel provides FaceSenseViewModel with viewModelScope. */
val appModule = module {
    viewModel { FaceSenseHostViewModel() }
}

class FaceSenseHostViewModel : ViewModel() {
    val faceSenseViewModel: FaceSenseViewModel by lazy {
        GlobalContext.get().get()
    }
}
