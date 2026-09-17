package com.example.ai

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.localization.AppLanguage
import com.example.models.CropType

enum class AiStatus {
  OFFLINE_READY,    // 🟢 Offline AI Ready (Local model installed & verified)
  ONLINE_READY,     // 🟡 Online AI (Cloud Gemini active)
  UNAVAILABLE       // 🔴 AI Unavailable (Neither local nor online network reachable)
}

class CropAiManager(private val context: Context) {
  val localProvider = LocalOfflineModelProvider()
  val onlineProvider = GeminiOnlineProvider()

  fun getAiStatus(): AiStatus {
    if (localProvider.isAvailable()) {
      return AiStatus.OFFLINE_READY
    }
    if (isNetworkConnected() && onlineProvider.isAvailable()) {
      return AiStatus.ONLINE_READY
    }
    return AiStatus.UNAVAILABLE
  }

  fun getStatusLabel(status: AiStatus, lang: AppLanguage): String {
    return when (status) {
      AiStatus.OFFLINE_READY -> "🟢 Offline AI Ready"
      AiStatus.ONLINE_READY -> "🟡 Online AI (Cloud Gemini)"
      AiStatus.UNAVAILABLE -> "🔴 AI Unavailable"
    }
  }

  suspend fun analyze(
    bitmap: Bitmap,
    selectedCrop: CropType,
    language: AppLanguage
  ): CropAnalysisResult {
    // 1. Attempt Local Offline Model first
    if (localProvider.isAvailable()) {
      return localProvider.analyzeCrop(bitmap, selectedCrop, language)
    }

    // 2. Check Network & Online Fallback
    if (isNetworkConnected() && onlineProvider.isAvailable()) {
      return onlineProvider.analyzeCrop(bitmap, selectedCrop, language)
    }

    // 3. Neither available - return honest error, DO NOT fake predictions
    return CropAnalysisResult(
      outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
      rawMessage = "Offline AI model is not available. Please install or load the local crop model.",
      sourceProvider = "AI System"
    )
  }

  private fun isNetworkConnected(): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val activeNetwork = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
  }
}
