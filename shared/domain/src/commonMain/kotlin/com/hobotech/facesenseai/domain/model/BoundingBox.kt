package com.hobotech.facesenseai.domain.model

/**
 * Bounding box in image pixel coordinates (e.g. from ML Kit face detection).
 */
data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)
