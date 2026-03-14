package com.hobotech.facesenseai.domain.usecase

import com.hobotech.facesenseai.domain.model.GreetingMessage
import com.hobotech.facesenseai.domain.repository.GreetingRepository

class GetGreetingUseCase(private val repository: GreetingRepository) {
    suspend operator fun invoke(): GreetingMessage = repository.getGreeting()
}
