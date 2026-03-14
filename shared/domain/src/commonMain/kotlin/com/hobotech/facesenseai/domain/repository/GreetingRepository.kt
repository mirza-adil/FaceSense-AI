package com.hobotech.facesenseai.domain.repository

import com.hobotech.facesenseai.domain.model.GreetingMessage

interface GreetingRepository {
    suspend fun getGreeting(): GreetingMessage
}
