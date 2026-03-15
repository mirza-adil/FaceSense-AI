package com.hobotech.facesenseai.camera

import com.hobotech.facesenseai.domain.model.LivenessResult

/** iOS stub; replace with AVFoundation when needed. */
actual fun createCameraController(): CameraController = IosCameraController()

private class IosCameraController : CameraController {
    private var callback: ((Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit)? = null

    override fun startPreview() {}
    override fun stopPreview() {}
    override fun setFrameCallback(callback: (Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit) {
        this.callback = callback
    }
}
