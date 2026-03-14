package com.hobotech.facesenseai.facesense

import androidx.compose.runtime.Composable
import com.hobotech.facesenseai.di.FaceSenseHostViewModel
import com.hobotech.facesenseai.facesense.presentation.FaceSenseViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun faceSenseViewModel(): FaceSenseViewModel =
    koinViewModel<FaceSenseHostViewModel>().faceSenseViewModel
