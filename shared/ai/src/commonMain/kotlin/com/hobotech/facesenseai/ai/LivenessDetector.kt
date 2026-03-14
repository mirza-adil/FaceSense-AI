package com.hobotech.facesenseai.ai

import com.hobotech.facesenseai.domain.liveness.LivenessDetector

/**
 * Platform-specific factory for liveness detection (TFLite on Android, stub on iOS).
 */
expect fun createLivenessDetector(): LivenessDetector
