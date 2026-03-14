package com.hobotech.facesenseai.ai

import android.content.Context
import com.hobotech.facesenseai.domain.liveness.LivenessDetector
import com.hobotech.facesenseai.domain.model.LivenessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual fun createLivenessDetector(): LivenessDetector = TfliteLivenessDetector()

private class TfliteLivenessDetector : LivenessDetector {
    private var interpreter: Interpreter? = null

    companion object {
        private const val INPUT_SIZE = 64
        private const val INPUT_CHANNELS = 3
        private const val MODEL_ASSET = "liveness_model.tflite"
    }

    override fun setContext(ctx: Any?) {
        val context = ctx as? Context ?: return
        try {
            context.assets.open(MODEL_ASSET).use { stream ->
                val modelBytes = stream.readBytes()
                val buffer = ByteBuffer.allocateDirect(modelBytes.size).order(ByteOrder.nativeOrder())
                buffer.put(modelBytes)
                buffer.rewind()
                interpreter = Interpreter(buffer)
            }
        } catch (_: Exception) {
            interpreter = null
        }
    }

    override suspend fun detect(rgbBytes: ByteArray, width: Int, height: Int): LivenessResult =
        withContext(Dispatchers.Default) {
            val interp = interpreter ?: return@withContext LivenessResult(isLive = true, confidence = 0.5f)
            if (rgbBytes.size < width * height * INPUT_CHANNELS) {
                return@withContext LivenessResult(isLive = true, confidence = 0.5f)
            }
            try {
                val inputSize = 1 * INPUT_SIZE * INPUT_SIZE * INPUT_CHANNELS
                val inputBuffer = ByteBuffer.allocateDirect(inputSize * 4).order(ByteOrder.nativeOrder())
                val scale = width.coerceAtLeast(1) to height.coerceAtLeast(1)
                for (y in 0 until INPUT_SIZE) {
                    for (x in 0 until INPUT_SIZE) {
                        val srcX = (x * scale.first) / INPUT_SIZE
                        val srcY = (y * scale.second) / INPUT_SIZE
                        val idx = (srcY * width + srcX) * INPUT_CHANNELS
                        if (idx + 2 < rgbBytes.size) {
                            val r = (rgbBytes[idx].toInt() and 0xFF) / 255f
                            val g = (rgbBytes[idx + 1].toInt() and 0xFF) / 255f
                            val b = (rgbBytes[idx + 2].toInt() and 0xFF) / 255f
                            inputBuffer.putFloat(r).putFloat(g).putFloat(b)
                        } else {
                            inputBuffer.putFloat(0.5f).putFloat(0.5f).putFloat(0.5f)
                        }
                    }
                }
                inputBuffer.rewind()
                val outputBuffer = Array(1) { FloatArray(1) }
                interp.run(inputBuffer, outputBuffer)
                val liveProb = outputBuffer[0][0].coerceIn(0f, 1f)
                LivenessResult(isLive = liveProb >= 0.5f, confidence = liveProb)
            } catch (_: Exception) {
                LivenessResult(isLive = true, confidence = 0.5f)
            }
        }
}
