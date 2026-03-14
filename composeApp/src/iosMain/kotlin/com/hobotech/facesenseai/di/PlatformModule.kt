package com.hobotech.facesenseai.di

import com.hobotech.facesenseai.camera.createCameraController
import org.koin.dsl.module

fun platformModule() = module {
    single { createCameraController() }
}
