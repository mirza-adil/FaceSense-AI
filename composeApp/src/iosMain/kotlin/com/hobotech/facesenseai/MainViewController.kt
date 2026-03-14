package com.hobotech.facesenseai

import androidx.compose.ui.window.ComposeUIViewController
import com.hobotech.facesenseai.di.domainModule
import com.hobotech.facesenseai.di.initKoin
import com.hobotech.facesenseai.di.platformModule
import com.hobotech.facesenseai.facesense.di.faceSenseModule

private val koinInitialized = run {
    initKoin(listOf(platformModule(), domainModule, faceSenseModule))
}

fun MainViewController() = ComposeUIViewController {
    koinInitialized
    App()
}