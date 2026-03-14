package com.hobotech.facesenseai

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific camera preview surface.
 * Android: PreviewView for CameraX. iOS: placeholder.
 */
@Composable
expect fun CameraPreviewSection(modifier: Modifier = Modifier)
