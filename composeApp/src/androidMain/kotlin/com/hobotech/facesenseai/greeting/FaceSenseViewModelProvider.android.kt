package com.hobotech.facesenseai.greeting

import androidx.compose.runtime.Composable
import com.hobotech.facesenseai.di.FaceSenseHostViewModel
import com.hobotech.facesenseai.greeting.presentation.FaceSenseViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun faceSenseViewModel(): FaceSenseViewModel =
    koinViewModel<FaceSenseHostViewModel>().faceSenseViewModel
