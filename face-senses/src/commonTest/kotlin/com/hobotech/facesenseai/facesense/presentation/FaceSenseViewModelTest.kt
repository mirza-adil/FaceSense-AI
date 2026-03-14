package com.hobotech.facesenseai.facesense.presentation

import com.hobotech.facesenseai.camera.CameraController
import com.hobotech.facesenseai.camera.FaceAttributes
import com.hobotech.facesenseai.camera.FaceBounds
import com.hobotech.facesenseai.camera.Frame
import com.hobotech.facesenseai.domain.model.LivenessResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class FaceSenseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeCameraController = object : CameraController {
        override fun startPreview() {}
        override fun stopPreview() {}
        override fun setFrameCallback(callback: (Frame, List<FaceBounds>, List<LivenessResult>, List<FaceAttributes>) -> Unit) {}
    }

    @Test
    fun startCamera_updatesIsFrontCameraFromController() = runTest {
        val viewModel = FaceSenseViewModel(fakeCameraController)
        viewModel.startCamera()
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.state.first()
        assertEquals(false, state.isFrontCamera)
        assertNull(state.error)
    }

    @Test
    fun stopCamera_clearsDetectionsAndFrameSize() = runTest {
        val viewModel = FaceSenseViewModel(fakeCameraController)
        viewModel.stopCamera()
        val state = viewModel.state.first()
        assertEquals(emptyList(), state.detections)
        assertEquals(0, state.frameWidth)
        assertEquals(0, state.frameHeight)
    }
}
