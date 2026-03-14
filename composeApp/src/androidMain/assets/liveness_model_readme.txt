Place liveness_model.tflite here for live vs spoof face detection.

Expected model:
- Input: [1, 64, 64, 3] float32 (RGB image, normalized 0-1)
- Output: [1, 1] float32 (live probability 0-1; >= 0.5 = live)

If the file is missing, the app returns a default "live" result (confidence 0.5).
