package com.hobotech.facesenseai.domain.model

data class FaceAnalysisResult(
    val label: String,
    val score: Float,
    val boundingBox: BoundingBox? = null,
    val liveness: LivenessResult? = null
)
