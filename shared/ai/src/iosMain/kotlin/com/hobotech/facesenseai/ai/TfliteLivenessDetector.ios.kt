package com.hobotech.facesenseai.ai

import com.hobotech.facesenseai.domain.liveness.LivenessDetector
import com.hobotech.facesenseai.domain.model.LivenessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** iOS stub; replace with Core ML / TFLite when needed. */
actual fun createLivenessDetector(): LivenessDetector = IosLivenessDetector()

private class IosLivenessDetector : LivenessDetector {
    override suspend fun detect(rgbBytes: ByteArray, width: Int, height: Int): LivenessResult =
        withContext(Dispatchers.Default) {
            LivenessResult(isLive = true, confidence = 0.5f)
        }
}
