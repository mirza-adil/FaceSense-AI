package com.hobotech.facesenseai.di

import com.hobotech.facesenseai.data.repository.GreetingRepositoryImpl
import com.hobotech.facesenseai.domain.repository.GreetingRepository
import com.hobotech.facesenseai.domain.usecase.GetGreetingUseCase
import org.koin.dsl.module

/**
 * Shared Koin modules for domain and data layers.
 * PlatformDataSource must be provided by the app module (androidMain/iosMain).
 */
val domainModule = module {
    single<GreetingRepository> { GreetingRepositoryImpl(get()) }
    factory { GetGreetingUseCase(get()) }
}
