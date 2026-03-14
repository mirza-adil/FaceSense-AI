package com.hobotech.facesenseai.facesense.presentation

import com.hobotech.facesenseai.camera.CameraController
import com.hobotech.facesenseai.camera.FaceAttributes
import com.hobotech.facesenseai.camera.FaceBounds
import com.hobotech.facesenseai.camera.Frame
import com.hobotech.facesenseai.domain.model.BoundingBox
import com.hobotech.facesenseai.domain.model.FaceAnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val EYE_CLOSED_THRESHOLD = 0.4f
private const val EYE_OPEN_THRESHOLD = 0.5f
private const val SMILE_THRESHOLD = 0.6f
private const val MOVE_CLOSER_RATIO = 1.2f
private const val HEAD_MOVE_FRACTION = 0.10f
private const val HEAD_TURN_REFERENCE_SAMPLES = 15

class FaceSenseViewModel(
    private val cameraController: CameraController
) {
    private val _state = MutableStateFlow(FaceSenseUiState())
    val state: StateFlow<FaceSenseUiState> = _state.asStateFlow()

    private var blinkEyesWereClosed = false
    private var headTurnCenterHistory = mutableListOf<Float>()
    private var headTurnReference: Float? = null
    private var headTurnSawLeft = false
    private var headTurnSawRight = false
    private var baselineFaceArea: Float? = null

    fun startCamera() {
        resetActiveLiveness()
        cameraController.setFrameCallback(::onFrame)
        cameraController.startPreview()
        _state.value = _state.value.copy(isFrontCamera = cameraController.isFrontCamera())
    }

    fun switchCamera() {
        cameraController.switchCamera()
        _state.value = _state.value.copy(isFrontCamera = cameraController.isFrontCamera())
    }

    fun stopCamera() {
        cameraController.stopPreview()
        _state.value = _state.value.copy(
            detections = emptyList(),
            isAnalyzing = false,
            frameWidth = 0,
            frameHeight = 0
        )
        resetActiveLiveness()
    }

    private fun resetActiveLiveness() {
        blinkEyesWereClosed = false
        headTurnCenterHistory.clear()
        headTurnReference = null
        headTurnSawLeft = false
        headTurnSawRight = false
        baselineFaceArea = null
        _state.value = _state.value.copy(
            activeLiveness = ActiveLivenessState()
        )
    }

    private fun onFrame(
        frame: Frame,
        faceBounds: List<FaceBounds>,
        livenessList: List<com.hobotech.facesenseai.domain.model.LivenessResult>,
        attributesList: List<FaceAttributes>
    ) {
        val detections = faceBounds.mapIndexed { index, bounds ->
            val liveness = livenessList.getOrNull(index)
            FaceAnalysisResult(
                label = "face",
                score = 1f,
                boundingBox = BoundingBox(bounds.left, bounds.top, bounds.right, bounds.bottom),
                liveness = liveness
            )
        }

        val attrs = attributesList.firstOrNull()
        val bounds = faceBounds.firstOrNull()
        val al = _state.value.activeLiveness

        if (al.passed != null) {
            _state.value = _state.value.copy(
                detections = detections,
                frameWidth = frame.width,
                frameHeight = frame.height,
                isAnalyzing = false
            )
            return
        }

        var blinkDone = al.blinkDone
        var headTurnDone = al.headTurnDone
        var smileDone = al.smileDone
        var moveCloserDone = al.moveCloserDone
        var passed: Boolean? = al.passed

        if (attrs != null && bounds != null) {
            val leftEye = attrs.leftEyeOpenProbability ?: 0.5f
            val rightEye = attrs.rightEyeOpenProbability ?: 0.5f
            val smiling = attrs.smilingProbability ?: 0f

            if (!blinkDone) {
                if (leftEye < EYE_CLOSED_THRESHOLD && rightEye < EYE_CLOSED_THRESHOLD) {
                    blinkEyesWereClosed = true
                }
                if (blinkEyesWereClosed && leftEye > EYE_OPEN_THRESHOLD && rightEye > EYE_OPEN_THRESHOLD) {
                    blinkDone = true
                }
            }

            if (!smileDone && smiling > SMILE_THRESHOLD) {
                smileDone = true
            }

            val centerX = (bounds.left + bounds.right) / 2f
            val area = (bounds.right - bounds.left) * (bounds.bottom - bounds.top)
            if (baselineFaceArea == null && area > 0f) baselineFaceArea = area
            if (!moveCloserDone && baselineFaceArea != null && area >= baselineFaceArea!! * MOVE_CLOSER_RATIO) {
                moveCloserDone = true
            }

            if (!headTurnDone) {
                headTurnCenterHistory.add(centerX)
                if (headTurnCenterHistory.size > 60) headTurnCenterHistory.removeAt(0)
                val imgWidth = frame.width.toFloat().coerceAtLeast(1f)
                val threshold = imgWidth * HEAD_MOVE_FRACTION
                if (headTurnReference == null && headTurnCenterHistory.size >= HEAD_TURN_REFERENCE_SAMPLES) {
                    val sample = headTurnCenterHistory.take(HEAD_TURN_REFERENCE_SAMPLES)
                    headTurnReference = sample.sum() / sample.size
                }
                headTurnReference?.let { ref ->
                    if (centerX <= ref - threshold) headTurnSawLeft = true
                    if (centerX >= ref + threshold) headTurnSawRight = true
                    if (headTurnSawLeft && headTurnSawRight) headTurnDone = true
                }
            }

            if (blinkDone && headTurnDone && smileDone && moveCloserDone) {
                passed = true
            }
        }

        _state.value = _state.value.copy(
            detections = detections,
            frameWidth = frame.width,
            frameHeight = frame.height,
            isAnalyzing = false,
            activeLiveness = ActiveLivenessState(
                blinkDone = blinkDone,
                headTurnDone = headTurnDone,
                smileDone = smileDone,
                moveCloserDone = moveCloserDone,
                passed = passed
            )
        )
    }
}
