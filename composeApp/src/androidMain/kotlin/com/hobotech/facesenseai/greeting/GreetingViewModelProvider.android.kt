package com.hobotech.facesenseai.greeting

import androidx.compose.runtime.Composable
import com.hobotech.facesenseai.di.GreetingHostViewModel
import com.hobotech.facesenseai.greeting.presentation.GreetingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun greetingViewModel(): GreetingViewModel =
    koinViewModel<GreetingHostViewModel>().greetingViewModel
