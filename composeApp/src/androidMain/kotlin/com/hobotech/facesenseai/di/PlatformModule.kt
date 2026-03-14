package com.hobotech.facesenseai.di

import com.hobotech.facesenseai.ai.createLivenessDetector
import com.hobotech.facesenseai.camera.CameraController
import com.hobotech.facesenseai.camera.createCameraController
import com.hobotech.facesenseai.domain.liveness.LivenessDetector
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun platformModule() = module {
    single<LivenessDetector> {
        createLivenessDetector().apply { setContext(androidContext()) }
    }
    single<CameraController> {
        createCameraController().apply { setLivenessDetector(get<LivenessDetector>()) }
    }
}
