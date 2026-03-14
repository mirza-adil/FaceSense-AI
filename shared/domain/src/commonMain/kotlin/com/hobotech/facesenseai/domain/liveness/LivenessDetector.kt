package com.hobotech.facesenseai.domain.liveness

import com.hobotech.facesenseai.domain.model.LivenessResult

/**
 * Liveness detection: classifies a face crop as live (real person) or spoof (photo/video/mask).
 * Implementations use TensorFlow Lite or similar on-device models.
 */
interface LivenessDetector {
    /** Optional: on Android, set Context to load model from assets. No-op on iOS. */
    fun setContext(ctx: Any?) {}
    /**
     * Run liveness on an RGB image crop (e.g. 64x64 or 128x128).
     * @param rgbBytes RGB bytes, row-major, size = width * height * 3
     */
    suspend fun detect(rgbBytes: ByteArray, width: Int, height: Int): LivenessResult
}
