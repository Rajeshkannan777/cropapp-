package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.localization.AppLanguage
import com.example.models.CropType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiOnlineProvider : CropAiProvider {
  override val providerName: String = "Online Cloud AI (Gemini)"
  override val isLocalOffline: Boolean = false

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  override fun isAvailable(): Boolean {
    val key = BuildConfig.GEMINI_API_KEY
    return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
  }

  override suspend fun analyzeCrop(
    bitmap: Bitmap,
    selectedCrop: CropType,
    language: AppLanguage
  ): CropAnalysisResult = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext CropAnalysisResult(
        outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
        rawMessage = "Gemini API key is not configured. Please add your key in AI Studio Secrets panel, or install the offline local model.",
        sourceProvider = providerName
      )
    }

    try {
      // 1. Prepare Base64 Image
      val base64Image = bitmapToBase64(bitmap)

      // 2. Formulate 3-Step Verification Prompt
      val prompt = buildAnalysisPrompt(selectedCrop, language)

      // 3. Build REST Request Payload for Gemini 2.5 Flash
      val requestJson = JSONObject().apply {
        val contentsArray = JSONArray().apply {
          val contentObj = JSONObject().apply {
            val partsArray = JSONArray().apply {
              put(JSONObject().put("text", prompt))
              put(
                JSONObject().put(
                  "inlineData",
                  JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", base64Image)
                  }
                )
              )
            }
            put("parts", partsArray)
          }
          put(contentObj)
        }
        put("contents", contentsArray)

        put(
          "generationConfig",
          JSONObject().apply {
            put("temperature", 0.2)
            put("responseMimeType", "application/json")
          }
        )
      }

      val mediaType = "application/json; charset=utf-8".toMediaType()
      val body = requestJson.toString().toRequestBody(mediaType)
      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

      val httpRequest = Request.Builder()
        .url(url)
        .post(body)
        .build()

      val response = client.newCall(httpRequest).execute()
      val responseString = response.body?.string()

      if (!response.isSuccessful || responseString.isNullOrBlank()) {
        return@withContext CropAnalysisResult(
          outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
          rawMessage = "Cloud AI service error (${response.code}). Check internet connection or retry.",
          sourceProvider = providerName
        )
      }

      parseGeminiResponse(responseString, selectedCrop, language)
    } catch (e: Exception) {
      CropAnalysisResult(
        outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
        rawMessage = "Network error: ${e.localizedMessage ?: "Unable to contact AI service"}",
        sourceProvider = providerName
      )
    }
  }

  private fun buildAnalysisPrompt(selectedCrop: CropType, language: AppLanguage): String {
    return """
You are CropGuard AI, a professional agricultural pathologist and field agronomist.
A farmer has submitted an image for inspection.
The farmer selected the crop: "${selectedCrop.displayName}".
The farmer's requested language is: "${language.displayName}" (${language.nativeName}).

Follow this mandatory 3-step evaluation protocol strictly:

STEP 1 — CROP VALIDATION:
Determine whether the image actually contains a recognizable agricultural crop, plant, leaf, stem, or fruit from one of these 7 supported crops:
- Tomato
- Potato
- Rice
- Maize
- Wheat
- Cotton
- Chilli

If the image does NOT contain a recognizable crop/plant (e.g. human/person, animal, vehicle, building, food dish, consumer product, landscape without crops, screenshot, document, completely blank, or extremely blurry):
Return JSON:
{
  "valid_crop": false,
  "detected_crop": "None",
  "outcome": "INVALID_IMAGE",
  "message": "No supported crop was detected. Please upload a clear image of a crop or leaf."
}
Translate "message" into natural ${language.displayName}. Do not perform disease diagnosis on an invalid image.

STEP 2 — CROP IDENTIFICATION:
If a plant is detected, identify which supported crop it is (Tomato, Potato, Rice, Maize, Wheat, Cotton, Chilli, or Unknown).
Compare the detected crop with the farmer's selection ("${selectedCrop.displayName}"):
- If the detected crop does not match the selected crop (e.g. farmer selected Tomato, but the image appears to contain Rice or Cotton):
Return JSON:
{
  "valid_crop": true,
  "detected_crop": "<detected crop name in English>",
  "outcome": "CROP_MISMATCH",
  "message": "Crop mismatch detected. You selected ${selectedCrop.displayName}, but the uploaded image appears to contain <detected crop in ${language.displayName}>. Please select the correct crop or upload another image."
}
- If the crop cannot be identified with reasonable confidence:
Return JSON:
{
  "valid_crop": false,
  "detected_crop": "Unknown",
  "outcome": "UNCLEAR_CROP",
  "message": "Crop could not be identified confidently. Please upload a clearer image."
}

STEP 3 — DISEASE AND PEST ANALYSIS:
Only if the image is a valid crop AND matches "${selectedCrop.displayName}":
Analyze the image for:
- Crop diseases (fungal, bacterial, viral)
- Pest infestations (borers, whiteflies, aphids, caterpillars, mites)
- Nutrient deficiency symptoms (nitrogen, iron, phosphorus, etc.)
- Visible plant stress / scorch
- Healthy condition

If the image is too ambiguous to determine the condition safely:
Return JSON:
{
  "valid_crop": true,
  "detected_crop": "${selectedCrop.displayName}",
  "outcome": "UNCLEAR_CONDITION",
  "message": "Unable to determine the condition confidently. Please upload a clear close-up image of the affected leaf, stem, fruit or plant."
}

If a condition or healthy state is diagnosed:
Return JSON:
{
  "valid_crop": true,
  "detected_crop": "${selectedCrop.displayName}",
  "outcome": "SUCCESS",
  "disease_or_pest": "<Name in ${language.displayName} with scientific name in parentheses, e.g. Early Blight (Alternaria solani) or Healthy Foliage>",
  "category": "<Fungal Disease / Bacterial Disease / Viral Disease / Pest Infestation / Nutrient Deficiency / Healthy>",
  "confidence": "<e.g. 91% or 84%>",
  "severity": "<Low / Moderate / Severe / None (Healthy)>",
  "risk_level": "<Low Risk / Watch List / Action Required / Immediate Containment>",
  "symptoms": "<Observed symptoms clearly written in ${language.displayName}>",
  "recommendation": "<Farmer-friendly cultural, organic, or advisory recommendation in ${language.displayName}. Must include notice to consult local agricultural officer before spraying chemical pesticides.>"
}

Ensure all farmer-facing text (disease_or_pest, symptoms, recommendation, message) is translated into natural, easy-to-understand ${language.displayName} suitable for farmers. Return only valid JSON.
""".trimIndent()
  }

  private fun parseGeminiResponse(
    responseString: String,
    selectedCrop: CropType,
    language: AppLanguage
  ): CropAnalysisResult {
    try {
      val root = JSONObject(responseString)
      val candidates = root.optJSONArray("candidates") ?: return CropAnalysisResult(
        outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
        rawMessage = "No response from AI model",
        sourceProvider = providerName
      )

      val first = candidates.optJSONObject(0) ?: return CropAnalysisResult(
        outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
        rawMessage = "Empty candidate response from AI model",
        sourceProvider = providerName
      )

      val content = first.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      val text = parts?.optJSONObject(0)?.optString("text") ?: ""

      // Clean markdown codeblocks if present
      val cleanJson = text.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

      val json = JSONObject(cleanJson)
      val outcomeStr = json.optString("outcome", "SUCCESS").uppercase()
      val validCrop = json.optBoolean("valid_crop", true)
      val detectedCrop = json.optString("detected_crop", selectedCrop.displayName)
      val message = json.optString("message", "")

      val outcome = when (outcomeStr) {
        "INVALID_IMAGE" -> AnalysisOutcome.INVALID_IMAGE
        "CROP_MISMATCH" -> AnalysisOutcome.CROP_MISMATCH
        "UNCLEAR_CROP" -> AnalysisOutcome.UNCLEAR_CROP
        "UNCLEAR_CONDITION" -> AnalysisOutcome.UNCLEAR_CONDITION
        else -> AnalysisOutcome.SUCCESS
      }

      return CropAnalysisResult(
        outcome = outcome,
        validCrop = validCrop,
        detectedCrop = detectedCrop,
        selectedCrop = selectedCrop.displayName,
        diseaseOrPest = json.optString("disease_or_pest", ""),
        category = json.optString("category", "Agricultural Foliage Analysis"),
        confidence = json.optString("confidence", "88%"),
        severity = json.optString("severity", "Moderate"),
        riskLevel = json.optString("risk_level", "Action Required"),
        symptoms = json.optString("symptoms", ""),
        recommendation = json.optString("recommendation", ""),
        rawMessage = message,
        sourceProvider = providerName
      )
    } catch (e: Exception) {
      return CropAnalysisResult(
        outcome = AnalysisOutcome.AI_PROVIDER_ERROR,
        rawMessage = "Could not parse AI response: ${e.message}",
        sourceProvider = providerName
      )
    }
  }

  private fun bitmapToBase64(bitmap: Bitmap): String {
    val maxDim = 1024
    val scaledBitmap = if (bitmap.width > maxDim || bitmap.height > maxDim) {
      val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
      val newWidth = if (ratio > 1) maxDim else (maxDim * ratio).toInt()
      val newHeight = if (ratio > 1) (maxDim / ratio).toInt() else maxDim
      Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    } else {
      bitmap
    }

    val stream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
  }
}
