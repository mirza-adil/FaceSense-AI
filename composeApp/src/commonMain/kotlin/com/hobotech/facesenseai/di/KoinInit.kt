package com.hobotech.facesenseai.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * Initializes Koin with the given list of modules.
 * Called from Android MainActivity and iOS MainViewController with platform-specific modules.
 */
fun initKoin(appModules: List<Module>) {
    startKoin {
        modules(appModules)
    }
}
