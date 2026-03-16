package com.hobotech.facesenseai

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.hobotech.facesenseai.camera.CameraController
import org.koin.core.context.GlobalContext

@Composable
actual fun CameraPreviewSection(modifier: Modifier) {
    // Check if we are in Edit Mode (Preview) to avoid Koin initialization issues
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(Color.DarkGray))
        return
    }

    val controller = remember { GlobalContext.get().get<CameraController>() }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).also { controller.setPreviewTarget(it) }
        }
    )
}
