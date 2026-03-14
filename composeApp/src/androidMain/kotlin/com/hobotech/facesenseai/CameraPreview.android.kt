package com.hobotech.facesenseai

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.hobotech.facesenseai.camera.CameraController
import org.koin.core.context.GlobalContext

@Composable
actual fun CameraPreviewSection(modifier: Modifier) {
    val controller = remember { GlobalContext.get().get<CameraController>() }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).also { controller.setPreviewTarget(it) }
        }
    )
}
