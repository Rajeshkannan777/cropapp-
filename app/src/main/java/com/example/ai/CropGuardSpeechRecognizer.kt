package com.example.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.localization.AppLanguage
import java.util.Locale

sealed class VoiceIntent {
  object OpenWeather : VoiceIntent()
  object OpenMap : VoiceIntent()
  object StartDetection : VoiceIntent()
  object ShowHistory : VoiceIntent()
  object ShowAlerts : VoiceIntent()
  object ReadAlerts : VoiceIntent()
  object StopSpeaking : VoiceIntent()
  data class SelectCrop(val cropName: String) : VoiceIntent()
  data class Unknown(val rawText: String) : VoiceIntent()
}

class CropGuardSpeechRecognizer(private val context: Context) {
  private var recognizer: SpeechRecognizer? = null

  var isListening by mutableStateOf(false)
    private set

  var liveTranscript by mutableStateOf("")
    private set

  var lastDetectedIntent by mutableStateOf<VoiceIntent?>(null)
    private set

  var errorMessage by mutableStateOf<String?>(null)
    private set

  val isAvailable: Boolean
    get() = SpeechRecognizer.isRecognitionAvailable(context)

  fun startListening(
    language: AppLanguage = AppLanguage.ENGLISH,
    onIntentDetected: (VoiceIntent) -> Unit = {}
  ) {
    errorMessage = null
    liveTranscript = ""
    lastDetectedIntent = null

    if (!isAvailable) {
      errorMessage = "Speech recognition service is not available on this device."
      return
    }

    try {
      if (recognizer == null) {
        recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
      }

      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
          RecognizerIntent.EXTRA_LANGUAGE_MODEL,
          RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.locale.toLanguageTag())
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language.locale.toLanguageTag())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
      }

      recognizer?.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
          isListening = true
        }

        override fun onBeginningOfSpeech() {
          isListening = true
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
          isListening = false
        }

        override fun onError(error: Int) {
          isListening = false
          errorMessage = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard. Please tap and try again."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
              "Network connection issue for voice recognition."
            else -> "Speech recognition error (Code: $error)"
          }
        }

        override fun onResults(results: Bundle?) {
          isListening = false
          val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          val transcript = matches?.firstOrNull() ?: ""
          if (transcript.isNotBlank()) {
            liveTranscript = transcript
            val detected = parseIntent(transcript)
            lastDetectedIntent = detected
            onIntentDetected(detected)
          }
        }

        override fun onPartialResults(partialResults: Bundle?) {
          val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          matches?.firstOrNull()?.let {
            liveTranscript = it
          }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
      })

      recognizer?.startListening(intent)
      isListening = true
    } catch (e: Exception) {
      isListening = false
      errorMessage = "Could not initialize voice recognition: ${e.localizedMessage}"
    }
  }

  fun stopListening() {
    try {
      recognizer?.stopListening()
    } catch (e: Exception) {
      // Ignored
    }
    isListening = false
  }

  fun destroy() {
    try {
      recognizer?.destroy()
    } catch (e: Exception) {
      // Ignored
    }
    recognizer = null
    isListening = false
  }

  fun parseIntent(transcript: String): VoiceIntent {
    val t = transcript.trim().lowercase(Locale.getDefault())

    // 1. Weather
    if (listOf("weather", "climate", "rain", "forecast", "வானிலை", "मौसम", "వాతావరణం", "ಹವಾಮಾನ", "കാലാവസ്ഥ", "আবহাওয়া", "हवामान")
        .any { t.contains(it) }) {
      return VoiceIntent.OpenWeather
    }

    // 2. Map / Location
    if (listOf("map", "risk", "location", "வரைபடம்", "நக்ஷா", "म్యాಪ್", "నక్ష", "പటం", "মানচিত্র", "नकाशा", "இருப்பிடம்", "स्थान")
        .any { t.contains(it) }) {
      return VoiceIntent.OpenMap
    }

    // 3. Take photo / AI Crop Detection
    if (listOf("photo", "camera", "scan", "detect", "analyze", "leaf", "படம்", "புகைப்படம்", "கேமரா", "फोटो", "कैमरा", "जांच", "ఫోటో", "కెమెరా", "ಫೋಟೋ", "ക്യാമറ", "ছবি", "तपासा")
        .any { t.contains(it) }) {
      return VoiceIntent.StartDetection
    }

    // 4. Alerts
    if (listOf("alert", "warning", "எச்சரிக்கை", "अलर्ट", "चेतावनी", "హెచ్చరిక", "ಎಚ್ಚರಿಕೆ", "മുന്നറിയിപ്പ്", "সতর্কতা", "इशारा")
        .any { t.contains(it) }) {
      return if (listOf("read", "speak", "வாசி", "படி", "पढ़ो", "సునావో", "చదువు", "ఓది", "വായിക്കുക", "শোনান", "वाचा").any { t.contains(it) }) {
        VoiceIntent.ReadAlerts
      } else {
        VoiceIntent.ShowAlerts
      }
    }

    // 5. History
    if (listOf("history", "record", "past", "வரலாறு", "इतिहास", "చరిత్ర", "ಇತಿಹಾಸ", "ചരിത്രം", "ইতিহাস")
        .any { t.contains(it) }) {
      return VoiceIntent.ShowHistory
    }

    // 6. Stop
    if (listOf("stop", "quiet", "silent", "நிறுத்து", "रोको", "आపు", "नಿಲ್ಲಿಸು", "നിർത്തുക", "থামো", "थांबवा")
        .any { t.contains(it) }) {
      return VoiceIntent.StopSpeaking
    }

    // 7. Crop selection
    val crops = listOf("tomato", "potato", "rice", "wheat", "maize", "cotton", "chilli")
    for (crop in crops) {
      if (t.contains(crop)) {
        return VoiceIntent.SelectCrop(crop)
      }
    }

    return VoiceIntent.Unknown(transcript)
  }
}
