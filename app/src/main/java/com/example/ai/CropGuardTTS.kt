package com.example.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.localization.AppLanguage
import java.util.Locale

class CropGuardTTS(private val context: Context) {
  private var tts: TextToSpeech? = null
  var isInitialized by mutableStateOf(false)
    private set
  var isSpeaking by mutableStateOf(false)
    private set
  var lastSpeechError by mutableStateOf<String?>(null)
    private set

  init {
    initialize()
  }

  private fun initialize() {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        isInitialized = true
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) {
            isSpeaking = true
            lastSpeechError = null
          }

          override fun onDone(utteranceId: String?) {
            isSpeaking = false
          }

          @Deprecated("Deprecated in Java")
          override fun onError(utteranceId: String?) {
            isSpeaking = false
          }

          override fun onError(utteranceId: String?, errorCode: Int) {
            isSpeaking = false
          }
        })
      }
    }
  }

  fun speak(text: String, language: AppLanguage, onUnavailable: (String) -> Unit = {}) {
    if (!isInitialized || tts == null) {
      onUnavailable("Text-to-speech engine is initializing...")
      return
    }

    val targetLocale = language.locale
    val result = tts?.setLanguage(targetLocale)

    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
      // Try fallback to language only without country (e.g. Locale("ta") instead of "ta-IN")
      val langOnly = Locale(language.code)
      val secondAttempt = tts?.setLanguage(langOnly)
      if (secondAttempt == TextToSpeech.LANG_MISSING_DATA || secondAttempt == TextToSpeech.LANG_NOT_SUPPORTED) {
        lastSpeechError = "Speech voice for ${language.displayName} (${language.nativeName}) is not installed on this device."
        onUnavailable(lastSpeechError!!)
        return
      }
    }

    tts?.stop()
    isSpeaking = true
    lastSpeechError = null
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CropGuard_${System.currentTimeMillis()}")
  }

  fun stop() {
    tts?.stop()
    isSpeaking = false
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}
