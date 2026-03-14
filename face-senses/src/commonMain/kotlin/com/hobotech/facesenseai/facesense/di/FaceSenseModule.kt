package com.hobotech.facesenseai.facesense.di

import com.hobotech.facesenseai.facesense.presentation.FaceSenseViewModel
import org.koin.dsl.module

val faceSenseModule = module {
    factory { FaceSenseViewModel(get()) }
}
