package com.hobotech.facesenseai.facesense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hobotech.facesenseai.facesense.presentation.FaceSenseViewModel
import org.koin.core.context.GlobalContext

@Composable
actual fun faceSenseViewModel(): FaceSenseViewModel = remember {
    GlobalContext.get().get()
}
