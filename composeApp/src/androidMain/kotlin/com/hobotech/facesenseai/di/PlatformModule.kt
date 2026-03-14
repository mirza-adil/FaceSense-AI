package com.hobotech.facesenseai.di

import com.hobotech.facesenseai.data.source.PlatformDataSource
import com.hobotech.facesenseai.getPlatform
import org.koin.dsl.module

fun platformModule() = module {
    single<PlatformDataSource> { AndroidPlatformDataSource() }
}

private class AndroidPlatformDataSource : PlatformDataSource {
    override fun getPlatformName(): String = getPlatform().name
}
