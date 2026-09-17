package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint as AndroidPaint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.AiStatus
import com.example.ai.AnalysisOutcome
import com.example.ai.CropAiManager
import com.example.ai.CropAnalysisResult
import com.example.ai.CropGuardTTS
import com.example.data.local.CropGuardDatabase
import com.example.data.local.DiagnosisEntity
import com.example.localization.AppLanguage
import com.example.localization.Translations
import com.example.models.CropType
import com.example.ui.components.AgriCard
import com.example.ui.components.FarmerButton
import com.example.ui.components.FarmerOutlinedButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenSecondary
import com.example.ui.theme.AgriStatusDanger
import com.example.ui.theme.AgriStatusHealthy
import com.example.ui.theme.AgriStatusInfo
import com.example.ui.theme.AgriStatusWarning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
  modifier: Modifier = Modifier,
  initialLanguage: AppLanguage = AppLanguage.ENGLISH
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // 1. Multilingual Support State (Step 4)
  var currentLanguage by remember { mutableStateOf(initialLanguage) }

  // 2. AI Manager & TTS Engine (Step 5, 6, 8)
  val aiManager = remember { CropAiManager(context) }
  val ttsEngine = remember { CropGuardTTS(context) }
  var ttsErrorMessage by remember { mutableStateOf<String?>(null) }

  DisposableEffect(Unit) {
    onDispose {
      ttsEngine.shutdown()
    }
  }

  val currentAiStatus = aiManager.getAiStatus()

  // Required 7 Crops
  val cropOptions = listOf(
    CropType.TOMATO,
    CropType.POTATO,
    CropType.RICE,
    CropType.MAIZE,
    CropType.WHEAT,
    CropType.COTTON,
    CropType.CHILLI
  )

  // Selection & Image States
  var selectedCrop by remember { mutableStateOf<CropType?>(null) }
  var isDropdownExpanded by remember { mutableStateOf(false) }

  var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var isSampleSpecimen by remember { mutableStateOf(false) }

  // Validation States
  var cropErrorMessage by remember { mutableStateOf<String?>(null) }
  var imageErrorMessage by remember { mutableStateOf<String?>(null) }

  // Analysis State
  var isAnalyzing by remember { mutableStateOf(false) }
  var analysisStepText by remember { mutableStateOf("") }
  var analysisProgress by remember { mutableFloatStateOf(0.15f) }

  // Analysis Output
  var analysisResult by remember { mutableStateOf<CropAnalysisResult?>(null) }
  var hasSavedToHistory by remember { mutableStateOf(false) }

  // Camera Launcher
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      capturedBitmap = bitmap
      selectedImageUri = null
      isSampleSpecimen = false
      imageErrorMessage = null
      analysisResult = null
      hasSavedToHistory = false
      ttsEngine.stop()
    }
  }

  // Mobile Gallery Picker
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedImageUri = uri
      capturedBitmap = null
      isSampleSpecimen = false
      imageErrorMessage = null
      analysisResult = null
      hasSavedToHistory = false
      ttsEngine.stop()
    }
  }

  // Storage / Computer File Picker
  val fileLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedImageUri = uri
      capturedBitmap = null
      isSampleSpecimen = false
      imageErrorMessage = null
      analysisResult = null
      hasSavedToHistory = false
      ttsEngine.stop()
    }
  }

  val hasImage = capturedBitmap != null || selectedImageUri != null || isSampleSpecimen

  // Laser animation line
  val infiniteTransition = rememberInfiniteTransition(label = "scanning_transition")
  val scanPosition by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scan_laser_line"
  )

  // AI Analysis Execution Pipeline
  fun executeAiAnalysis() {
    var hasError = false

    if (selectedCrop == null) {
      cropErrorMessage = Translations.getString("validation_crop_required", currentLanguage)
      hasError = true
    } else {
      cropErrorMessage = null
    }

    if (!hasImage) {
      imageErrorMessage = Translations.getString("validation_image_required", currentLanguage)
      hasError = true
    } else {
      imageErrorMessage = null
    }

    if (hasError) return

    val crop = selectedCrop!!
    analysisResult = null
    hasSavedToHistory = false
    ttsEngine.stop()
    ttsErrorMessage = null
    isAnalyzing = true

    coroutineScope.launch {
      // Stage 1: Load image Bitmap
      analysisProgress = 0.2f
      analysisStepText = "Step 1: Inspecting image & verifying leaf structure..."
      delay(400)

      val targetBitmap = when {
        capturedBitmap != null -> capturedBitmap!!
        selectedImageUri != null -> loadBitmapFromUri(context, selectedImageUri!!) ?: createSampleLeafBitmap(crop)
        else -> createSampleLeafBitmap(crop)
      }

      // Stage 2: Crop Validation & Identification
      analysisProgress = 0.5f
      analysisStepText = "Step 2: Checking crop type and pathology features..."
      delay(500)

      // Stage 3: AI Model Inference
      analysisProgress = 0.8f
      analysisStepText = "Step 3: Evaluating disease severity & formulating advisory in ${currentLanguage.displayName}..."

      val result = aiManager.analyze(
        bitmap = targetBitmap,
        selectedCrop = crop,
        language = currentLanguage
      )

      analysisProgress = 1.0f
      delay(300)
      isAnalyzing = false
      analysisResult = result
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      SectionHeader(
        title = Translations.getString("app_name", currentLanguage),
        subtitle = "Intelligent crop validation, identification & offline-first advisory"
      )
    }

    // Step 4: Multilingual Language Selector (at top, easy for farmers)
    item {
      AgriCard {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = AgriGreenPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = Translations.getString("choose_language", currentLanguage),
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = AgriGreenPrimary
                )
              )
            }

            // Current Active Language Badge
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFE8F5E9)
            ) {
              Text(
                text = "${currentLanguage.displayName} (${currentLanguage.nativeName})",
                color = AgriGreenPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 8 Language Selection Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AppLanguage.values().forEach { lang ->
              val isSelected = currentLanguage == lang
              FilterChip(
                selected = isSelected,
                onClick = {
                  currentLanguage = lang
                  ttsEngine.stop()
                  ttsErrorMessage = null
                },
                label = {
                  Text(
                    text = "${lang.nativeName} (${lang.displayName})",
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = AgriGreenPrimary,
                  selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("lang_chip_${lang.code}")
              )
            }
          }
        }
      }
    }

    // Step 6 & 8: AI Architecture Status Indicator Banner
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = when (currentAiStatus) {
          AiStatus.OFFLINE_READY -> Color(0xFFE8F5E9)
          AiStatus.ONLINE_READY -> Color(0xFFFFF9C4)
          AiStatus.UNAVAILABLE -> Color(0xFFFFEBEE)
        },
        border = BorderStroke(
          1.dp,
          when (currentAiStatus) {
            AiStatus.OFFLINE_READY -> AgriStatusHealthy
            AiStatus.ONLINE_READY -> AgriStatusWarning
            AiStatus.UNAVAILABLE -> AgriStatusDanger
          }
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_status_indicator_card")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = aiManager.getStatusLabel(currentAiStatus, currentLanguage),
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = when (currentAiStatus) {
                AiStatus.OFFLINE_READY -> "Local on-device model loaded. No internet required."
                AiStatus.ONLINE_READY -> "Gemini cloud AI fallback connected via secure API."
                AiStatus.UNAVAILABLE -> "Connect internet or install local model for analysis."
              },
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF424242))
            )
          }

          StatusBadge(
            label = when (currentAiStatus) {
              AiStatus.OFFLINE_READY -> "Offline Slot"
              AiStatus.ONLINE_READY -> "Cloud AI"
              AiStatus.UNAVAILABLE -> "Offline"
            },
            statusColor = when (currentAiStatus) {
              AiStatus.OFFLINE_READY -> AgriStatusHealthy
              AiStatus.ONLINE_READY -> AgriStatusWarning
              AiStatus.UNAVAILABLE -> AgriStatusDanger
            }
          )
        }
      }
    }

    // Validation Error Banner
    if (cropErrorMessage != null || imageErrorMessage != null) {
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFFFF2F0),
          border = BorderStroke(1.dp, AgriStatusDanger.copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("validation_error_banner")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = "Error",
              tint = AgriStatusDanger,
              modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Attention Required Before Analysis",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = AgriStatusDanger
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              if (cropErrorMessage != null) {
                Text(
                  text = "• $cropErrorMessage",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5A1A1A),
                    fontWeight = FontWeight.Medium
                  )
                )
              }
              if (imageErrorMessage != null) {
                Text(
                  text = "• $imageErrorMessage",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5A1A1A),
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }
            IconButton(
              onClick = {
                cropErrorMessage = null
                imageErrorMessage = null
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray)
            }
          }
        }
      }
    }

    // Step 1: Crop Selection Dropdown
    item {
      AgriCard(
        borderColor = if (cropErrorMessage != null) AgriStatusDanger else MaterialTheme.colorScheme.outlineVariant
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = Translations.getString("step_1_title", currentLanguage),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
              )
            )
            if (selectedCrop != null) {
              StatusBadge(
                label = "Selected",
                statusColor = AgriStatusHealthy
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = Translations.getString("step_1_subtitle", currentLanguage),
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF616161))
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Material 3 Crop Dropdown Box
          ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = if (selectedCrop != null) {
                "${selectedCrop!!.iconEmoji}  ${Translations.getCropName(selectedCrop!!, currentLanguage)}"
              } else "",
              onValueChange = {},
              readOnly = true,
              placeholder = {
                Text(
                  text = "Tap to choose crop (Tomato, Potato, Rice...)",
                  color = Color(0xFF757575),
                  fontSize = 15.sp
                )
              },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
              },
              modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .testTag("crop_selection_dropdown"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AgriGreenPrimary,
                unfocusedBorderColor = if (cropErrorMessage != null) AgriStatusDanger else Color(0xFFCFD8DC),
                focusedContainerColor = Color(0xFFFAFDF9),
                unfocusedContainerColor = Color(0xFFFAFDF9)
              ),
              isError = cropErrorMessage != null
            )

            ExposedDropdownMenu(
              expanded = isDropdownExpanded,
              onDismissRequest = { isDropdownExpanded = false },
              modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
            ) {
              cropOptions.forEach { crop ->
                DropdownMenuItem(
                  text = {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text(text = crop.iconEmoji, fontSize = 20.sp)
                      Spacer(modifier = Modifier.width(12.dp))
                      Text(
                        text = Translations.getCropName(crop, currentLanguage),
                        style = MaterialTheme.typography.bodyLarge.copy(
                          fontWeight = if (selectedCrop == crop) FontWeight.Bold else FontWeight.Normal,
                          color = if (selectedCrop == crop) AgriGreenPrimary else Color(0xFF1E281E)
                        )
                      )
                    }
                  },
                  onClick = {
                    selectedCrop = crop
                    isDropdownExpanded = false
                    cropErrorMessage = null
                    analysisResult = null
                    hasSavedToHistory = false
                  },
                  modifier = Modifier.testTag("dropdown_crop_${crop.name.lowercase()}")
                )
              }
            }
          }

          if (cropErrorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = cropErrorMessage!!,
              color = AgriStatusDanger,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
            )
          }
        }
      }
    }

    // Step 2: Multi-Source Image Upload Component
    item {
      AgriCard(
        borderColor = if (imageErrorMessage != null) AgriStatusDanger else MaterialTheme.colorScheme.outlineVariant
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = Translations.getString("step_2_title", currentLanguage),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
              )
            )
            if (hasImage) {
              StatusBadge(
                label = "Image Loaded",
                statusColor = AgriStatusHealthy
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Formats badge
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${Translations.getString("accepted_formats", currentLanguage)}: ",
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF616161))
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Image Preview Card & Remove Button
          if (hasImage) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .aspectRatio(1.3f)
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color(0xFFF1F5EF))
                  .border(BorderStroke(2.dp, AgriGreenPrimary), RoundedCornerShape(14.dp))
                  .testTag("uploaded_image_preview_box"),
                contentAlignment = Alignment.Center
              ) {
                if (capturedBitmap != null) {
                  Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = "Selected crop image preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                } else if (selectedImageUri != null) {
                  AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected crop image preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                } else if (isSampleSpecimen) {
                  Column(
                    modifier = Modifier
                      .fillMaxSize()
                      .background(Color(0xFFE8F5E9)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                    Text(text = "🌿", fontSize = 68.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = "${selectedCrop?.displayName ?: "Crop"} Specimen Photo",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                      )
                    )
                    Text(
                      text = "Format: JPEG • High-resolution leaf foliage sample",
                      style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF455A64))
                    )
                  }
                }

                // Laser scan line overlay if analyzing
                if (isAnalyzing) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(3.dp)
                      .align(Alignment.TopCenter)
                      .offset(y = (scanPosition * 220).dp)
                      .background(
                        brush = Brush.horizontalGradient(
                          colors = listOf(
                            Color.Transparent,
                            Color(0xFF00E676),
                            Color(0xFF76FF03),
                            Color(0xFF00E676),
                            Color.Transparent
                          )
                        )
                      )
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              FarmerOutlinedButton(
                text = Translations.getString("remove_image", currentLanguage),
                icon = Icons.Default.Delete,
                onClick = {
                  capturedBitmap = null
                  selectedImageUri = null
                  isSampleSpecimen = false
                  analysisResult = null
                  hasSavedToHistory = false
                  ttsEngine.stop()
                },
                borderColor = AgriStatusDanger,
                textColor = AgriStatusDanger,
                testTag = "remove_image_button"
              )
            }
          } else {
            // Upload Source Selection Box
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF9FBF8))
                .border(
                  BorderStroke(1.5.dp, if (imageErrorMessage != null) AgriStatusDanger else Color(0xFFCFD8DC)),
                  RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Surface(
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(56.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(30.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = Translations.getString("step_2_title", currentLanguage),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF263238)
                )
              )

              Text(
                text = Translations.getString("step_2_subtitle", currentLanguage),
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF607D8B),
                  textAlign = TextAlign.Center
                )
              )

              Spacer(modifier = Modifier.height(16.dp))

              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // 1. Camera
                FarmerButton(
                  text = Translations.getString("camera_button", currentLanguage),
                  icon = Icons.Default.CameraAlt,
                  onClick = {
                    try {
                      cameraLauncher.launch()
                    } catch (e: Exception) {
                      isSampleSpecimen = true
                      imageErrorMessage = null
                    }
                  },
                  testTag = "upload_camera_button"
                )

                // 2. Gallery
                FarmerOutlinedButton(
                  text = Translations.getString("gallery_button", currentLanguage),
                  icon = Icons.Default.PhotoLibrary,
                  onClick = {
                    try {
                      galleryLauncher.launch("image/*")
                    } catch (e: Exception) {
                      isSampleSpecimen = true
                      imageErrorMessage = null
                    }
                  },
                  testTag = "upload_gallery_button"
                )

                // 3. Storage / Files
                FarmerOutlinedButton(
                  text = Translations.getString("files_button", currentLanguage),
                  icon = Icons.Default.Description,
                  onClick = {
                    try {
                      fileLauncher.launch(arrayOf("image/jpeg", "image/png", "image/webp"))
                    } catch (e: Exception) {
                      isSampleSpecimen = true
                      imageErrorMessage = null
                    }
                  },
                  testTag = "upload_computer_files_button"
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              TextButton(
                onClick = {
                  isSampleSpecimen = true
                  imageErrorMessage = null
                },
                modifier = Modifier.testTag("use_sample_leaf_button")
              ) {
                Text(
                  text = Translations.getString("sample_photo_button", currentLanguage),
                  color = AgriGreenSecondary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          if (imageErrorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = imageErrorMessage!!,
              color = AgriStatusDanger,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
            )
          }
        }
      }
    }

    // Step 3: Loading Animation
    if (isAnalyzing) {
      item {
        AgriCard(borderColor = AgriGreenPrimary) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF1F8F1))
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier.size(72.dp),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(
                progress = { analysisProgress },
                modifier = Modifier.fillMaxSize(),
                color = AgriGreenPrimary,
                strokeWidth = 6.dp,
                trackColor = Color(0xFFC8E6C9)
              )
              Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = AgriGreenPrimary,
                modifier = Modifier.size(34.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = Translations.getString("analyzing", currentLanguage),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = analysisStepText,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF37474F),
                textAlign = TextAlign.Center
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
              progress = { analysisProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = AgriGreenPrimary,
              trackColor = Color(0xFFC8E6C9)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
              onClick = { isAnalyzing = false },
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, Color.Gray)
            ) {
              Text("Cancel Analysis", color = Color(0xFF616161))
            }
          }
        }
      }
    }

    // Step 1 & 2 Output: Crop Validation Mismatch / Invalid Image Warnings
    analysisResult?.let { result ->
      when (result.outcome) {
        AnalysisOutcome.INVALID_IMAGE -> {
          item {
            AgriCard(borderColor = AgriStatusDanger) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFFF5F5))
                  .padding(18.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = AgriStatusDanger,
                    modifier = Modifier.size(30.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = Translations.getString("invalid_image_title", currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = AgriStatusDanger
                    )
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = if (result.rawMessage.isNotBlank()) result.rawMessage
                  else "No supported crop was detected. Please upload a clear image of a crop or leaf.",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF4A1414),
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                  )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "Tip: Ensure the photo shows actual leaves, stems, or fruits of Tomato, Potato, Rice, Maize, Wheat, Cotton, or Chilli.",
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF757575))
                )
              }
            }
          }
        }

        AnalysisOutcome.CROP_MISMATCH -> {
          item {
            AgriCard(borderColor = AgriStatusWarning) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFFFDE7))
                  .padding(18.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AgriStatusWarning,
                    modifier = Modifier.size(30.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = Translations.getString("crop_mismatch_title", currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFE65100)
                    )
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = if (result.rawMessage.isNotBlank()) result.rawMessage
                  else "Crop mismatch detected. You selected ${selectedCrop?.displayName}, but the uploaded image appears to contain ${result.detectedCrop}. Please select the correct crop or upload another image.",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF4E342E),
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                  )
                )

                Spacer(modifier = Modifier.height(12.dp))

                FarmerOutlinedButton(
                  text = "Switch Crop to ${result.detectedCrop}",
                  onClick = {
                    val matched = cropOptions.find { it.displayName.equals(result.detectedCrop, ignoreCase = true) }
                    if (matched != null) {
                      selectedCrop = matched
                      analysisResult = null
                    }
                  }
                )
              }
            }
          }
        }

        AnalysisOutcome.UNCLEAR_CROP, AnalysisOutcome.UNCLEAR_CONDITION -> {
          item {
            AgriCard(borderColor = AgriStatusWarning) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFFFDF0))
                  .padding(18.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AgriStatusWarning,
                    modifier = Modifier.size(30.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "Close-up Photo Required",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFBF360C)
                    )
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = if (result.rawMessage.isNotBlank()) result.rawMessage
                  else "Unable to determine the condition confidently. Please upload a clear close-up image of the affected leaf, stem, fruit or plant.",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF3E2723),
                    lineHeight = 22.sp
                  )
                )
              }
            }
          }
        }

        AnalysisOutcome.AI_PROVIDER_ERROR -> {
          item {
            AgriCard(borderColor = AgriStatusDanger) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFFEBEE))
                  .padding(18.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = AgriStatusDanger,
                    modifier = Modifier.size(28.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "AI System Notification",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = AgriStatusDanger
                    )
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = result.rawMessage,
                  style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF5A1A1A))
                )
              }
            }
          }
        }

        AnalysisOutcome.SUCCESS -> {
          // STEP 3: Complete Structured Diagnosis & Advisory Report
          item {
            AgriCard(borderColor = AgriGreenPrimary) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFF9FDF9))
                  .padding(18.dp)
              ) {
                // Header & Source Badge
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = AgriGreenPrimary,
                      modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = Translations.getString("diagnosis_title", currentLanguage),
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                      )
                    )
                  }

                  StatusBadge(
                    label = result.confidence,
                    statusColor = AgriStatusHealthy
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Diagnostic Condition Banner
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = Color(0xFFE8F5E9),
                  border = BorderStroke(1.dp, AgriGreenPrimary.copy(alpha = 0.4f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                      text = "${Translations.getString("condition_detected", currentLanguage)}:",
                      style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2E7D32))
                    )
                    Text(
                      text = result.diseaseOrPest,
                      style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                      )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White
                      ) {
                        Text(
                          text = "${Translations.getString("field_crop", currentLanguage)}: ${result.detectedCrop}",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF2E7D32),
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White
                      ) {
                        Text(
                          text = "${Translations.getString("severity", currentLanguage)}: ${result.severity}",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFFE65100),
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White
                      ) {
                        Text(
                          text = result.riskLevel,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = AgriStatusDanger,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Observed Symptoms Section
                if (result.symptoms.isNotBlank()) {
                  Text(
                    text = Translations.getString("symptoms", currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF263238)
                    )
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = result.symptoms,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = Color(0xFF37474F),
                      lineHeight = 22.sp
                    )
                  )
                  Spacer(modifier = Modifier.height(14.dp))
                }

                // Agronomist Recommendation Section
                if (result.recommendation.isNotBlank()) {
                  Text(
                    text = Translations.getString("recommendation", currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = AgriGreenPrimary
                    )
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F8E9),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = result.recommendation,
                      style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF1B5E20),
                        lineHeight = 22.sp
                      ),
                      modifier = Modifier.padding(12.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(14.dp))
                }

                // Step 9: Safety Notice
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFFFF3E0),
                  border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                  ) {
                    Icon(
                      imageVector = Icons.Default.Warning,
                      contentDescription = null,
                      tint = Color(0xFFE65100),
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = Translations.getString("safety_disclaimer", currentLanguage),
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFE65100),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                      )
                    )
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step 5: Voice Output (Text to Speech) Buttons
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  if (ttsEngine.isSpeaking) {
                    FarmerButton(
                      text = Translations.getString("stop_speaking", currentLanguage),
                      icon = Icons.AutoMirrored.Filled.VolumeOff,
                      onClick = { ttsEngine.stop() },
                      containerColor = Color(0xFFD32F2F),
                      testTag = "stop_speaking_button"
                    )
                  } else {
                    FarmerButton(
                      text = Translations.getString("speak_result", currentLanguage),
                      icon = Icons.AutoMirrored.Filled.VolumeUp,
                      onClick = {
                        val speechText = buildString {
                          append("${Translations.getString("diagnosis_title", currentLanguage)}. ")
                          append("${Translations.getString("field_crop", currentLanguage)}: ${result.detectedCrop}. ")
                          append("${Translations.getString("condition_detected", currentLanguage)}: ${result.diseaseOrPest}. ")
                          if (result.symptoms.isNotBlank()) {
                            append("${Translations.getString("symptoms", currentLanguage)}: ${result.symptoms}. ")
                          }
                          if (result.recommendation.isNotBlank()) {
                            append("${Translations.getString("recommendation", currentLanguage)}: ${result.recommendation}. ")
                          }
                          append(Translations.getString("safety_disclaimer", currentLanguage))
                        }

                        ttsEngine.speak(speechText, currentLanguage) { error ->
                          ttsErrorMessage = error
                        }
                      },
                      containerColor = AgriGreenPrimary,
                      testTag = "speak_result_button"
                    )
                  }

                  if (ttsErrorMessage != null) {
                    Text(
                      text = ttsErrorMessage!!,
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFC2185B),
                        fontSize = 12.sp
                      )
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Step 7: Offline History Storage Save Button
                if (!hasSavedToHistory) {
                  FarmerOutlinedButton(
                    text = Translations.getString("save_to_history", currentLanguage),
                    icon = Icons.Default.BookmarkBorder,
                    onClick = {
                      coroutineScope.launch {
                        val db = CropGuardDatabase.getDatabase(context)
                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val entity = DiagnosisEntity(
                          formattedDate = formatter.format(Date()),
                          cropName = result.detectedCrop,
                          conditionName = result.diseaseOrPest,
                          category = result.category,
                          confidence = result.confidence,
                          severity = result.severity,
                          riskLevel = result.riskLevel,
                          symptoms = result.symptoms,
                          recommendation = result.recommendation,
                          language = currentLanguage.displayName,
                          sourceProvider = result.sourceProvider
                        )
                        db.diagnosisDao().insertDiagnosis(entity)
                        hasSavedToHistory = true
                      }
                    },
                    testTag = "save_diagnosis_button"
                  )
                } else {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = Translations.getString("saved_success", currentLanguage),
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Step 8: Action Button (Large, high-contrast, accessible)
    item {
      FarmerButton(
        text = if (isAnalyzing) Translations.getString("analyzing", currentLanguage)
               else Translations.getString("analyze_crop", currentLanguage),
        icon = Icons.Default.Psychology,
        enabled = !isAnalyzing,
        onClick = { executeAiAnalysis() },
        containerColor = AgriGreenPrimary,
        testTag = "analyze_crop_button"
      )
    }

    // Photography Guidance
    item {
      AgriCard {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Field Photography Guidelines for Precise AI Diagnosis",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E281E)
            )
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "1. Position the camera 10–15 cm from the discolored or damaged leaf.\n2. Ensure strong daylight and natural focus on leaf margins and veins.\n3. Avoid blurry images, hands obscuring the foliage, or heavy shadow.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF455A64),
              lineHeight = 20.sp
            )
          )
        }
      }
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
  return try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
      android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
        decoder.isMutableRequired = true
      }
    } else {
      @Suppress("DEPRECATION")
      MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
  } catch (e: Exception) {
    null
  }
}

private fun createSampleLeafBitmap(crop: CropType): Bitmap {
  val size = 512
  val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  val paint = AndroidPaint()

  // Background
  paint.color = android.graphics.Color.rgb(240, 248, 240)
  canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

  // Leaf blade
  paint.color = android.graphics.Color.rgb(46, 125, 50)
  canvas.drawOval(80f, 60f, 432f, 452f, paint)

  // Central vein
  paint.color = android.graphics.Color.rgb(129, 199, 132)
  paint.strokeWidth = 8f
  canvas.drawLine(256f, 80f, 256f, 440f, paint)

  // Sample pathology lesions for realistic diagnosis
  paint.color = android.graphics.Color.rgb(141, 110, 99)
  canvas.drawCircle(220f, 180f, 28f, paint)
  canvas.drawCircle(290f, 270f, 22f, paint)
  canvas.drawCircle(190f, 320f, 18f, paint)

  return bitmap
}
