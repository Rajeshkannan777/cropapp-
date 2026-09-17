package com.example.ai

import android.graphics.Bitmap
import com.example.localization.AppLanguage
import com.example.models.CropType

interface CropAiProvider {
  val providerName: String
  val isLocalOffline: Boolean
  fun isAvailable(): Boolean

  suspend fun analyzeCrop(
    bitmap: Bitmap,
    selectedCrop: CropType,
    language: AppLanguage
  ): CropAnalysisResult
}
