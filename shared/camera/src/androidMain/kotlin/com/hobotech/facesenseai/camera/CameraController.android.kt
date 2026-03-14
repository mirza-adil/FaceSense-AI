package com.hobotech.facesenseai.camera

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.hobotech.facesenseai.domain.liveness.LivenessDetector
import com.hobotech.facesenseai.domain.model.LivenessResult
import kotlinx.coroutines.runBlocking

actual fun createCameraController(): CameraController = CameraXController()

private const val LIVENESS_CROP_SIZE = 64

private class CameraXController : CameraController {
    private var cameraProvider: ProcessCameraProvider? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var appContext: android.content.Context? = null
    private var frameCallback: ((Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit)? = null
    private var previewView: PreviewView? = null
    private var useFrontCamera: Boolean = false
    private var livenessDetector: LivenessDetector? = null

    private val faceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        FaceDetection.getClient(options)
    }

    override fun setLifecycleOwner(owner: Any?) {
        val o = owner as? LifecycleOwner ?: return
        lifecycleOwner = o
        appContext = (o as? android.content.Context) ?: (o as? android.app.Activity)?.applicationContext
    }

    override fun setPreviewTarget(target: Any?) {
        previewView = target as? PreviewView
    }

    override fun setLivenessDetector(detector: Any?) {
        livenessDetector = detector as? LivenessDetector
    }

    override fun switchCamera() {
        useFrontCamera = !useFrontCamera
        if (cameraProvider != null && lifecycleOwner != null && appContext != null) {
            startPreview()
        }
    }

    override fun isFrontCamera(): Boolean = useFrontCamera

    override fun startPreview() {
        val ctx = appContext ?: run {
            Log.w("CameraXController", "Context not set; call setLifecycleOwner(activity) first.")
            return
        }
        val owner = lifecycleOwner ?: run {
            Log.w("CameraXController", "LifecycleOwner not set; call setLifecycleOwner(activity) first.")
            return
        }
        var provider = cameraProvider
        if (provider == null) {
            provider = ProcessCameraProvider.getInstance(ctx).get()
            cameraProvider = provider
        }

        val selector = if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy: ImageProxy ->
                    val buffer = imageProxy.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val frame = Frame(
                        bytes = bytes,
                        width = imageProxy.width,
                        height = imageProxy.height,
                        format = ImageFormat.YUV_420_888
                    )
                    val inputImage = InputImage.fromMediaImage(
                        imageProxy.image!!,
                        imageProxy.imageInfo.rotationDegrees
                    )
                    faceDetector.process(inputImage)
                        .addOnSuccessListener { faces ->
                            val boxes = faces.map { face ->
                                val r = face.boundingBox
                                FaceBounds(
                                    left = r.left.toFloat(),
                                    top = r.top.toFloat(),
                                    right = r.right.toFloat(),
                                    bottom = r.bottom.toFloat()
                                )
                            }
                            val attributes = faces.map { face ->
                                FaceAttributes(
                                    leftEyeOpenProbability = face.leftEyeOpenProbability,
                                    rightEyeOpenProbability = face.rightEyeOpenProbability,
                                    smilingProbability = face.smilingProbability
                                )
                            }
                            val livenessList = runLivenessIfNeeded(imageProxy, boxes)
                            frameCallback?.invoke(frame, boxes, livenessList, attributes)
                        }
                        .addOnFailureListener {
                            frameCallback?.invoke(frame, emptyList(), emptyList(), emptyList())
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

        provider.unbindAll()

        val previewView = this.previewView
        if (previewView != null) {
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            provider.bindToLifecycle(owner, selector, preview, imageAnalyzer)
        } else {
            Log.w("CameraXController", "PreviewView not set; only analysis will run. Call setPreviewTarget(PreviewView) for on-screen preview.")
            provider.bindToLifecycle(owner, selector, imageAnalyzer)
        }
    }

    override fun stopPreview() {
        cameraProvider?.unbindAll()
        cameraProvider = null
    }

    override fun setFrameCallback(callback: (Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit) {
        frameCallback = callback
    }

    private fun runLivenessIfNeeded(proxy: ImageProxy, boxes: List<FaceBounds>): List<LivenessResult> {
        val detector = livenessDetector ?: return boxes.map { LivenessResult(isLive = true, confidence = 0.5f) }
        if (boxes.isEmpty()) return emptyList()
        val w = proxy.width
        val h = proxy.height
        val yBuffer = proxy.planes[0].buffer
        val uBuffer = proxy.planes[1].buffer
        val vBuffer = proxy.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uvSize = uBuffer.remaining()
        return boxes.map { bounds ->
            val cropRgb = yuvCropToRgb64(proxy, bounds, w, h, yBuffer, uBuffer, vBuffer)
            runBlocking {
                detector.detect(cropRgb, LIVENESS_CROP_SIZE, LIVENESS_CROP_SIZE)
            }
        }
    }

    private fun yuvCropToRgb64(
        proxy: ImageProxy,
        bounds: FaceBounds,
        imgW: Int,
        imgH: Int,
        yBuffer: java.nio.ByteBuffer,
        uBuffer: java.nio.ByteBuffer,
        vBuffer: java.nio.ByteBuffer
    ): ByteArray {
        val left = bounds.left.toInt().coerceIn(0, imgW - 1)
        val top = bounds.top.toInt().coerceIn(0, imgH - 1)
        val right = bounds.right.toInt().coerceIn(left + 1, imgW)
        val bottom = bounds.bottom.toInt().coerceIn(top + 1, imgH)
        val cropW = right - left
        val cropH = bottom - top
        val yRowStride = proxy.planes[0].rowStride
        val uRowStride = proxy.planes[1].rowStride
        val uPixelStride = proxy.planes[1].pixelStride
        val vRowStride = proxy.planes[2].rowStride
        val vPixelStride = proxy.planes[2].pixelStride
        val out = ByteArray(LIVENESS_CROP_SIZE * LIVENESS_CROP_SIZE * 3)
        for (oy in 0 until LIVENESS_CROP_SIZE) {
            for (ox in 0 until LIVENESS_CROP_SIZE) {
                val srcX = left + (ox * cropW) / LIVENESS_CROP_SIZE
                val srcY = top + (oy * cropH) / LIVENESS_CROP_SIZE
                val yIdx = srcY * yRowStride + srcX
                val uIdx = (srcY / 2) * uRowStride + (srcX / 2) * uPixelStride
                val vIdx = (srcY / 2) * vRowStride + (srcX / 2) * vPixelStride
                val y = (yBuffer.get(yIdx).toInt() and 0xFF)
                val u = (uBuffer.get(uIdx).toInt() and 0xFF) - 128
                val v = (vBuffer.get(vIdx).toInt() and 0xFF) - 128
                val r = (y + (1.370705 * v).toInt()).coerceIn(0, 255)
                val g = (y - (0.337633 * u + 0.698001 * v).toInt()).coerceIn(0, 255)
                val b = (y + (1.732446 * u).toInt()).coerceIn(0, 255)
                val outIdx = (oy * LIVENESS_CROP_SIZE + ox) * 3
                out[outIdx] = r.toByte()
                out[outIdx + 1] = g.toByte()
                out[outIdx + 2] = b.toByte()
            }
        }
        return out
    }
}
