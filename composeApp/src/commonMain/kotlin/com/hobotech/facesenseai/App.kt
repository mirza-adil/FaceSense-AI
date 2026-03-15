package com.hobotech.facesenseai

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hobotech.facesenseai.domain.model.BoundingBox
import com.hobotech.facesenseai.domain.model.FaceAnalysisResult
import com.hobotech.facesenseai.facesense.faceSenseViewModel
import com.hobotech.facesenseai.facesense.presentation.ActiveLivenessStep
import com.hobotech.facesenseai.facesense.presentation.ActiveLivenessState
import com.hobotech.facesenseai.facesense.presentation.FaceSenseUiState
import com.hobotech.facesenseai.theme.FaceSenseTheme

private val CardBorderWidth = 1.dp

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
@Preview
fun App(context: Any? = null) {
    FaceSenseTheme {
        Surface(
            modifier = Modifier.fillMaxSize().safeContentPadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel = faceSenseViewModel()
            var state by remember { mutableStateOf(viewModel.state.value) }
            LaunchedEffect(viewModel.state) {
                viewModel.state.collect { state = it }
            }
            LaunchedEffect(state.activeLiveness.passed) {
                if (state.activeLiveness.passed == true) {
                    showVerificationSuccessToast(context)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "FaceSense AI",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Secure face verification with liveness check",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SectionLabel(text = "Live view")
                Spacer(modifier = Modifier.height(8.dp))
                CameraPreviewCard(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                SectionLabel(text = "Status")
                Spacer(modifier = Modifier.height(8.dp))
                StatusCard(
                    faceCount = state.detections.size,
                    detections = state.detections,
                    activeLiveness = state.activeLiveness,
                    isAnalyzing = state.isAnalyzing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                SectionLabel(text = "Verification steps")
                Spacer(modifier = Modifier.height(8.dp))
                ActiveLivenessCard(
                    activeLiveness = state.activeLiveness,
                    modifier = Modifier.fillMaxWidth()
                )

                LivenessResultCard(
                    detection = state.detections.firstOrNull(),
                    activeLiveness = state.activeLiveness,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                SectionLabel(text = "Controls")
                Spacer(modifier = Modifier.height(8.dp))
                ControlButtons(
                    isFrontCamera = state.isFrontCamera,
                    onStartCamera = { viewModel.startCamera() },
                    onSwitchCamera = { viewModel.switchCamera() },
                    onStopCamera = { viewModel.stopCamera() },
                    modifier = Modifier.fillMaxWidth()
                )

                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CameraPreviewCard(
    state: FaceSenseUiState,
    modifier: Modifier = Modifier
) {
    val showPlaceholder = state.frameWidth == 0 && state.frameHeight == 0
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = modifier
            .border(
                width = CardBorderWidth,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
            contentAlignment = Alignment.Center
        ) {
            CameraPreviewSection(modifier = Modifier.fillMaxSize())

            if (showPlaceholder) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "Camera off",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap \"Start camera\" below to begin",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (!showPlaceholder) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 2.5.dp.toPx()
                    val fw = state.frameWidth.toFloat().coerceAtLeast(1f)
                    val fh = state.frameHeight.toFloat().coerceAtLeast(1f)
                    val scaleX = size.width / fw
                    val scaleY = size.height / fh
                    state.detections.forEach { detection ->
                        detection.boundingBox?.let { box ->
                            drawRect(
                                color = primaryColor,
                                topLeft = Offset(box.left * scaleX, box.top * scaleY),
                                size = Size(
                                    (box.right - box.left) * scaleX,
                                    (box.bottom - box.top) * scaleY
                                ),
                                style = Stroke(width = strokeWidth)
                            )
                        }
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = state.isAnalyzing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ScanningOverlay()
            }
        }
    }
}

@Composable
private fun ScanningOverlay() {
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 2000),
        label = "scanProgress"
    )
    Box(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(72.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Scanning…",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.95f)
            )
        }
    }
}

@Composable
private fun ActiveLivenessCard(
    activeLiveness: ActiveLivenessState,
    modifier: Modifier = Modifier
) {
    if (activeLiveness.passed != null) return
    val steps = ActiveLivenessStep.entries
    val doneCount = listOf(
        activeLiveness.blinkDone,
        activeLiveness.headTurnDone,
        activeLiveness.smileDone,
        activeLiveness.moveCloserDone
    ).count { it }
    val progress = if (steps.isEmpty()) 0f else doneCount.toFloat() / steps.size
    val shape = RoundedCornerShape(14.dp)
    Card(
        modifier = modifier.border(
            width = CardBorderWidth,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            shape = shape
        ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Follow these steps to verify",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$doneCount / ${steps.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            steps.forEachIndexed { index, step ->
                val done = when (step) {
                    ActiveLivenessStep.BLINK -> activeLiveness.blinkDone
                    ActiveLivenessStep.TURN_HEAD -> activeLiveness.headTurnDone
                    ActiveLivenessStep.SMILE -> activeLiveness.smileDone
                    ActiveLivenessStep.MOVE_CLOSER -> activeLiveness.moveCloserDone
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (done) "✓" else "${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (done) FontWeight.Bold else FontWeight.Medium,
                            color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = step.instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun LivenessResultCard(
    detection: FaceAnalysisResult?,
    activeLiveness: ActiveLivenessState,
    modifier: Modifier = Modifier
) {
    val fromActive = activeLiveness.passed != null
    if (!fromActive && detection?.liveness == null) return
    val isReal = if (fromActive) activeLiveness.passed == true else (detection?.liveness?.isLive == true)
    val confidencePercent = detection?.liveness?.let { (it.confidence * 100).toInt().coerceIn(0, 100) } ?: 100
    val accentColor = if (isReal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val shape = RoundedCornerShape(14.dp)
    Card(
        modifier = modifier.border(
            width = CardBorderWidth,
            color = accentColor.copy(alpha = 0.35f),
            shape = shape
        ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isReal) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .background(accentColor, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = if (isReal) "✓" else "✕",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isReal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = if (isReal) "Verified" else "Not verified",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isReal) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = if (isReal) {
                            if (fromActive) "All steps completed successfully" else "Live face detected"
                        } else {
                            if (fromActive) "Complete all steps to verify" else "Photo or screen may have been used"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isReal) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Text(
                text = "$confidencePercent%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isReal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun StatusCard(
    faceCount: Int,
    detections: List<com.hobotech.facesenseai.domain.model.FaceAnalysisResult>,
    activeLiveness: ActiveLivenessState,
    isAnalyzing: Boolean,
    modifier: Modifier = Modifier
) {
    val livenessSuffix = when {
        activeLiveness.passed == true -> " — Verified"
        activeLiveness.passed == false -> " — Not verified"
        else -> detections.firstOrNull()?.liveness?.let { if (it.isLive) " — Live" else " — Check required" } ?: ""
    }
    val (statusText, statusColor) = when {
        faceCount > 0 -> (if (faceCount == 1) "1 face detected$livenessSuffix" else "$faceCount faces detected$livenessSuffix") to MaterialTheme.colorScheme.primary
        isAnalyzing -> "Analyzing…" to MaterialTheme.colorScheme.primary
        else -> "Center your face in the frame" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    val shape = RoundedCornerShape(14.dp)
    Card(
        modifier = modifier.border(
            width = CardBorderWidth,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            shape = shape
        ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (faceCount > 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(6.dp)
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColor
                )
            }
            if (faceCount > 0) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ControlButtons(
    isFrontCamera: Boolean,
    onStartCamera: () -> Unit,
    onSwitchCamera: () -> Unit,
    onStopCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onStartCamera,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "Start camera",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onSwitchCamera,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isFrontCamera) "Back" else "Front",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            OutlinedButton(
                onClick = onStopCamera,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Stop",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun CameraPreviewCardPreview() {
    FaceSenseTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            CameraPreviewCard(
                state = FaceSenseUiState(
                    isAnalyzing = true,
                    detections = listOf(
                        FaceAnalysisResult(
                            label = "Face 1",
                            score = 0.95f,
                            boundingBox = BoundingBox(
                                left = 100f,
                                top = 100f,
                                right = 500f,
                                bottom = 700f
                            )
                        )
                    ),
                    frameWidth = 1080,
                    frameHeight = 1920
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        }
    }
}
