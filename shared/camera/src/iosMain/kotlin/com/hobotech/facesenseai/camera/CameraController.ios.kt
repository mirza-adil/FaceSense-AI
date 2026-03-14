package com.hobotech.facesenseai.camera

import com.hobotech.facesenseai.domain.model.LivenessResult

/**
 * iOS actual: stub implementation. Replace with AVFoundation-based implementation
 * (e.g. AVCaptureSession, AVCaptureVideoDataOutput) via Kotlin/Native interop or Swift.
 */
actual fun createCameraController(): CameraController = IosCameraController()

private class IosCameraController : CameraController {
    private var callback: ((Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit)? = null

    override fun startPreview() {
        // TODO: Start AVCaptureSession, add AVCaptureVideoDataOutput, set sample buffer delegate
    }

    override fun stopPreview() {
        // TODO: Stop AVCaptureSession
    }

    override fun setFrameCallback(callback: (Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit) {
        this.callback = callback
    }
}
