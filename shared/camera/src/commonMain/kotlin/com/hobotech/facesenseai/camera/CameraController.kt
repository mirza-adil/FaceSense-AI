package com.hobotech.facesenseai.camera

import com.hobotech.facesenseai.domain.model.LivenessResult

/**
 * Face bounding box in image pixel coordinates (e.g. from ML Kit).
 */
data class FaceBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

/**
 * Per-face attributes from ML Kit (for active liveness: blink, smile, etc.).
 * All values in 0f..1f; null when not available.
 */
data class FaceAttributes(
    val leftEyeOpenProbability: Float?,
    val rightEyeOpenProbability: Float?,
    val smilingProbability: Float?
)

/**
 * Abstraction for real-time camera frame capture.
 * Implementations: CameraX (Android), AVFoundation (iOS).
 * Callback receives frame, face bounds, liveness results, and face attributes (for active liveness).
 */
interface CameraController {
    fun startPreview()
    fun stopPreview()
    fun setFrameCallback(callback: (Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit)
    /** Optional: set liveness detector to run on each face crop (Android). No-op on iOS. */
    fun setLivenessDetector(detector: Any?) {}
    /** Optional: on Android, set LifecycleOwner before startPreview(). No-op on iOS. */
    fun setLifecycleOwner(owner: Any?) {}
    /** Optional: on Android, set PreviewView (or SurfaceProvider) so the camera feed is shown. No-op on iOS. */
    fun setPreviewTarget(target: Any?) {}
    /** Switch between front and back camera. No-op if not supported (e.g. iOS stub). */
    fun switchCamera() {}
    /** True when using front camera. */
    fun isFrontCamera(): Boolean = false
}

/**
 * Factory for platform-specific CameraController.
 * Business logic depends on this; tests inject a fake.
 */
expect fun createCameraController(): CameraController
