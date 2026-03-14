package com.hobotech.facesenseai.greeting.presentation

import com.hobotech.facesenseai.domain.model.GreetingMessage
import com.hobotech.facesenseai.domain.repository.GreetingRepository
import com.hobotech.facesenseai.domain.usecase.GetGreetingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class FaceSenseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Test
    fun load_success_updatesStateWithMessage() = runTest {
        val repository = object : GreetingRepository {
            override suspend fun getGreeting(): GreetingMessage = GreetingMessage("Hello, KMP!")
        }
        val useCase = GetGreetingUseCase(repository)
        val viewModel = FaceSenseViewModel(useCase, testScope)

        viewModel.load()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals("Hello, KMP!", state.message)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun load_failure_updatesStateWithError() = runTest {
        val repository = object : GreetingRepository {
            override suspend fun getGreeting(): GreetingMessage = throw RuntimeException("Network error")
        }
        val useCase = GetGreetingUseCase(repository)
        val viewModel = FaceSenseViewModel(useCase, testScope)

        viewModel.load()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals("Network error", state.error)
        assertEquals(false, state.isLoading)
    }
}
