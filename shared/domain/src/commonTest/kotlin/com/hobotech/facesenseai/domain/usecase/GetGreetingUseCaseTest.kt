package com.hobotech.facesenseai.domain.usecase

import com.hobotech.facesenseai.domain.model.GreetingMessage
import com.hobotech.facesenseai.domain.repository.GreetingRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetGreetingUseCaseTest {

    @Test
    fun invoke_returnsGreetingFromRepository() = runTest {
        val expected = GreetingMessage("Hello, Test!")
        val repository = object : GreetingRepository {
            override suspend fun getGreeting(): GreetingMessage = expected
        }
        val useCase = GetGreetingUseCase(repository)

        val result = useCase()

        assertEquals(expected, result)
        assertEquals("Hello, Test!", result.message)
    }
}
