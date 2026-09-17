package com.example.ai

enum class AnalysisOutcome {
  SUCCESS,
  INVALID_IMAGE,       // No plant or unsupported object (human, animal, car, etc.)
  CROP_MISMATCH,       // Selected crop differs from detected crop
  UNCLEAR_CROP,        // Crop cannot be identified confidently
  UNCLEAR_CONDITION,   // Condition cannot be determined confidently
  AI_PROVIDER_ERROR    // Network or model unavailable
}

data class CropAnalysisResult(
  val outcome: AnalysisOutcome,
  val validCrop: Boolean = false,
  val detectedCrop: String = "",
  val selectedCrop: String = "",
  val diseaseOrPest: String = "",
  val category: String = "",
  val confidence: String = "",
  val severity: String = "",
  val riskLevel: String = "",
  val symptoms: String = "",
  val recommendation: String = "",
  val rawMessage: String = "",
  val sourceProvider: String = "Online AI (Gemini 2.5 Flash)"
)
