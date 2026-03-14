package com.hobotech.facesenseai.greeting.presentation

import com.hobotech.facesenseai.domain.usecase.GetGreetingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FaceSenseViewModel(
    private val getGreetingUseCase: GetGreetingUseCase,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(FaceSenseUiState())
    val state: StateFlow<FaceSenseUiState> = _state.asStateFlow()

    fun load() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { getGreetingUseCase() }
                .onSuccess { _state.value = _state.value.copy(message = it.message, isLoading = false) }
                .onFailure { _state.value = _state.value.copy(error = it.message, isLoading = false) }
        }
    }
}
