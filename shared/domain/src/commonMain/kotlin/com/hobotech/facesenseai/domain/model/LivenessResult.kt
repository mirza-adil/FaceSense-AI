package com.hobotech.facesenseai.domain.model

/**
 * Result of liveness detection (real face vs photo/screen/mask).
 * Used to reduce spoofing in face verification flows.
 */
data class LivenessResult(
    val isLive: Boolean,
    val confidence: Float
)
