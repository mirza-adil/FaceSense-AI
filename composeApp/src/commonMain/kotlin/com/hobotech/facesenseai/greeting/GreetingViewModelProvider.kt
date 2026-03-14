package com.hobotech.facesenseai.greeting

import androidx.compose.runtime.Composable
import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel

/**
 * Expect/actual to obtain [GreetingViewModel] in a platform-appropriate way:
 * - Android: from a ViewModel that provides viewModelScope.
 * - iOS: from Koin with MainScope (no ViewModel lifecycle).
 */
@Composable
expect fun greetingViewModel(): GreetingViewModel
