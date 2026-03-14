package com.hobotech.facesenseai.ai

import com.hobotech.facesenseai.domain.liveness.LivenessDetector
import com.hobotech.facesenseai.domain.model.LivenessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * iOS actual: stub. Replace with TFLite iOS (Core ML or TFLite via CocoaPods) when needed.
 */
actual fun createLivenessDetector(): LivenessDetector = IosLivenessDetector()

private class IosLivenessDetector : LivenessDetector {
    override suspend fun detect(rgbBytes: ByteArray, width: Int, height: Int): LivenessResult =
        withContext(Dispatchers.Default) {
            LivenessResult(isLive = true, confidence = 0.5f)
        }
}
