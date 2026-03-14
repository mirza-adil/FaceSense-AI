package com.hobotech.facesenseai.greeting

import androidx.compose.runtime.Composable
import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel

/** Expect/actual: Android uses viewModelScope via GreetingHostViewModel; iOS uses Koin + MainScope. */
@Composable
expect fun greetingViewModel(): GreetingViewModel
