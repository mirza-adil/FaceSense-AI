package com.hobotech.facesenseai.data.repository

import com.hobotech.facesenseai.data.source.PlatformDataSource
import com.hobotech.facesenseai.domain.model.GreetingMessage
import com.hobotech.facesenseai.domain.repository.GreetingRepository

class GreetingRepositoryImpl(
    private val platformDataSource: PlatformDataSource
) : GreetingRepository {
    override suspend fun getGreeting(): GreetingMessage {
        val name = platformDataSource.getPlatformName()
        return GreetingMessage("Hello, $name!")
    }
}
