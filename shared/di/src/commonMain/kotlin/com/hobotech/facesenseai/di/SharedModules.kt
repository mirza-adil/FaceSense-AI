package com.hobotech.facesenseai.di

import com.hobotech.facesenseai.data.repository.GreetingRepositoryImpl
import com.hobotech.facesenseai.domain.repository.GreetingRepository
import com.hobotech.facesenseai.domain.usecase.GetGreetingUseCase
import org.koin.dsl.module

/** Domain and data Koin bindings. PlatformDataSource is provided by the app (androidMain/iosMain). */
val domainModule = module {
    single<GreetingRepository> { GreetingRepositoryImpl(get()) }
    factory { GetGreetingUseCase(get()) }
}
