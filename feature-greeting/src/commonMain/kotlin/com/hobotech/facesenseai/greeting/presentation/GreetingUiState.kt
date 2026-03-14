package com.hobotech.facesenseai.greeting.presentation

data class GreetingUiState(
    val message: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
