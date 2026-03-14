package com.hobotech.facesenseai.greeting.presentation

data class FaceSenseUiState(
    val message: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
