package com.hobotech.facesenseai.facesense.presentation

import com.hobotech.facesenseai.domain.model.FaceAnalysisResult

enum class ActiveLivenessStep(val instruction: String) {
    BLINK("Blink"),
    TURN_HEAD("Turn head left then right"),
    SMILE("Smile"),
    MOVE_CLOSER("Move closer to camera")
}

data class ActiveLivenessState(
    val blinkDone: Boolean = false,
    val headTurnDone: Boolean = false,
    val smileDone: Boolean = false,
    val moveCloserDone: Boolean = false,
    val passed: Boolean? = null
)

data class FaceSenseUiState(
    val error: String? = null,
    val detections: List<FaceAnalysisResult> = emptyList(),
    val isAnalyzing: Boolean = false,
    val isFrontCamera: Boolean = false,
    val frameWidth: Int = 0,
    val frameHeight: Int = 0,
    val activeLiveness: ActiveLivenessState = ActiveLivenessState()
)
