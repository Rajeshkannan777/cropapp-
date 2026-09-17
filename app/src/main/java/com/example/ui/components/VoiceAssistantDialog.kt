package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.ai.CropGuardSpeechRecognizer
import com.example.ai.VoiceIntent
import com.example.localization.AppLanguage
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriStatusDanger

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceAssistantDialog(
  onDismiss: () -> Unit,
  onIntentAction: (VoiceIntent) -> Unit,
  currentLanguage: AppLanguage = AppLanguage.ENGLISH
) {
  val context = LocalContext.current
  val speechRecognizer = remember { CropGuardSpeechRecognizer(context) }
  var selectedLanguage by remember { mutableStateOf(currentLanguage) }
  var permissionDenied by remember { mutableStateOf(false) }

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      permissionDenied = false
      speechRecognizer.startListening(selectedLanguage) { intent ->
        onIntentAction(intent)
      }
    } else {
      permissionDenied = true
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      speechRecognizer.destroy()
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (speechRecognizer.isListening) 1.25f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(700, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  Dialog(onDismissRequest = {
    speechRecognizer.stopListening()
    onDismiss()
  }) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("voice_assistant_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "🎤",
              fontSize = 20.sp,
              modifier = Modifier.padding(end = 6.dp)
            )
            Column {
              Text(
                text = "Voice Assistant",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = AgriGreenPrimary
                )
              )
              Text(
                text = "Hands-free farm navigation",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.Gray,
                  fontSize = 11.sp
                )
              )
            }
          }

          IconButton(onClick = {
            speechRecognizer.stopListening()
            onDismiss()
          }) {
            Icon(Icons.Default.Close, contentDescription = "Close dialog")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Selector Row
        Text(
          text = "Spoken Language: ${selectedLanguage.displayName} (${selectedLanguage.nativeName})",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E7D32)
          ),
          modifier = Modifier.padding(bottom = 6.dp)
        )

        // Large Microphone Pulse Button
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(110.dp)
            .padding(10.dp)
        ) {
          if (speechRecognizer.isListening) {
            Box(
              modifier = Modifier
                .size(90.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(AgriStatusDanger.copy(alpha = 0.25f))
            )
          }

          Surface(
            shape = CircleShape,
            color = if (speechRecognizer.isListening) AgriStatusDanger else AgriGreenPrimary,
            modifier = Modifier
              .size(72.dp)
              .clickable {
                if (speechRecognizer.isListening) {
                  speechRecognizer.stopListening()
                } else {
                  val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                  ) == PackageManager.PERMISSION_GRANTED

                  if (hasPermission) {
                    permissionDenied = false
                    speechRecognizer.startListening(selectedLanguage) { intent ->
                      onIntentAction(intent)
                    }
                  } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                  }
                }
              }
              .testTag("dialog_microphone_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (speechRecognizer.isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (speechRecognizer.isListening) "Stop Listening" else "Start Listening",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
              )
            }
          }
        }

        Text(
          text = if (speechRecognizer.isListening) "🎤 Listening... Speak now" else "Tap microphone to speak",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (speechRecognizer.isListening) AgriStatusDanger else Color(0xFF424242)
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Live Transcript Box
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF1F8E9),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Transcript:",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF558B2F))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (speechRecognizer.liveTranscript.isNotBlank()) {
                "\"${speechRecognizer.liveTranscript}\""
              } else {
                "Waiting for speech..."
              },
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B5E20)
              )
            )
          }
        }

        if (speechRecognizer.errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = speechRecognizer.errorMessage!!,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFFC62828),
              fontSize = 11.sp
            ),
            textAlign = TextAlign.Center
          )
        }

        if (permissionDenied) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Microphone permission is required for voice control. Please grant permission in device settings.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFFC62828),
              fontSize = 11.sp
            ),
            textAlign = TextAlign.Center
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Suggested Voice Command Chips
        Text(
          text = "Try saying:",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
          ),
          modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        val suggestions = listOf(
          "Open weather",
          "Take a photo",
          "Open risk map",
          "Show alerts",
          "Diagnosis history"
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          suggestions.forEach { command ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = Color(0xFFE8F5E9),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
              modifier = Modifier.clickable {
                val intent = speechRecognizer.parseIntent(command)
                onIntentAction(intent)
              }
            ) {
              Text(
                text = "🗣️ \"$command\"",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  color = Color(0xFF2E7D32)
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stop Listening / Close Button
        if (speechRecognizer.isListening) {
          Button(
            onClick = { speechRecognizer.stopListening() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("⏹ Stop Listening", color = Color.White)
          }
        }
      }
    }
  }
}
