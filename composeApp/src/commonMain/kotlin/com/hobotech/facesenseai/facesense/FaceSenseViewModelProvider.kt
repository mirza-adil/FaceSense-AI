package com.hobotech.facesenseai.facesense

import androidx.compose.runtime.Composable
import com.hobotech.facesenseai.facesense.presentation.FaceSenseViewModel

/** Expect/actual: Android uses viewModelScope via FaceSenseHostViewModel; iOS uses Koin + MainScope. */
@Composable
expect fun faceSenseViewModel(): FaceSenseViewModel
