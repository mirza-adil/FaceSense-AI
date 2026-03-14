package com.hobotech.facesenseai.greeting

import androidx.compose.runtime.Composable
import com.hobotech.facesenseai.greeting.presentation.FaceSenseViewModel

/** Expect/actual: Android uses viewModelScope via FaceSenseHostViewModel; iOS uses Koin + MainScope. */
@Composable
expect fun faceSenseViewModel(): FaceSenseViewModel
