package com.example.ai

import android.graphics.Bitmap
import com.example.localization.AppLanguage
import com.example.models.CropType

/**
 * Modular provider slot for an on-device local neural network
 * (e.g. TensorFlow Lite .tflite model or ONNX Runtime).
 *
 * Designed according to Step 6 of CropGuard architecture.
 * When a real weights file (e.g. crop_disease_v1.tflite) is placed into
 * assets or downloaded locally, isModelInstalled can be flagged true.
 * Until then, it correctly reports unavailable and does NOT fake predictions.
 */
class LocalOfflineModelProvider : CropAiProvider {
  override val providerName: String = "Offline Local Neural Model"
  override val isLocalOffline: Boolean = true

  // State flag for on-device model package
  var isModelInstalled: Boolean = false

  override fun isAvailable(): Boolean {
    return isModelInstalled
  }

  override suspend fun analyzeCrop(
    bitmap: Bitmap,
    selectedCrop: CropType,
    language: AppLanguage
  ): CropAnalysisResult {
    if (!isAvailable()) {
      return CropAnalysisResult(
        outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
        rawMessage = "Offline AI model is not available. Please install or load the local crop model.",
        sourceProvider = providerName
      )
    }

    // When a model file is loaded, local tensor inference executes here.
    return CropAnalysisResult(
      outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
      rawMessage = "Offline model loaded but inference pipeline awaiting model weights.",
      sourceProvider = providerName
    )
  }
}
