package com.hobotech.facesenseai.facesense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hobotech.facesenseai.facesense.presentation.FaceSenseViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private object FaceSenseKoinHelper : KoinComponent {
    fun getViewModel(): FaceSenseViewModel = get()
}

@Composable
actual fun faceSenseViewModel(): FaceSenseViewModel = remember {
    FaceSenseKoinHelper.getViewModel()
}
