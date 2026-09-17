package com.example.models

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AgriStatusDanger
import com.example.ui.theme.AgriStatusHealthy
import com.example.ui.theme.AgriStatusInfo
import com.example.ui.theme.AgriStatusWarning

enum class AppSection(val title: String, val shortTitle: String) {
  HOME("Home", "Home"),
  DETECTION("AI Crop Detection", "Detect"),
  WEATHER("Agri Weather", "Weather"),
  RISK_MAP("Farm Risk Map", "Risk Map"),
  ALERTS("Field Alerts", "Alerts"),
  HISTORY("Diagnosis History", "History")
}

enum class CropType(val displayName: String, val iconEmoji: String) {
  TOMATO("Tomato", "🍅"),
  POTATO("Potato", "🥔"),
  RICE("Rice", "🌱"),
  MAIZE("Maize", "🌽"),
  WHEAT("Wheat", "🌾"),
  COTTON("Cotton", "☁️"),
  CHILLI("Chilli", "🌶️")
}

enum class PlantPart(val displayName: String) {
  LEAF("Leaf"),
  STEM("Stem / Stalk"),
  FRUIT("Fruit / Grain"),
  ROOT("Root / Tuber"),
  WHOLE("Whole Plant")
}

enum class RiskLevel(val label: String, val color: Color) {
  LOW("Low Risk", AgriStatusHealthy),
  MODERATE("Medium Risk", AgriStatusWarning),
  HIGH("High Risk", AgriStatusDanger)
}

data class FarmZone(
  val id: String,
  val name: String,
  val crop: String,
  val acreage: Double,
  val riskLevel: RiskLevel,
  val riskFactors: List<String>,
  val soilMoisturePercent: Int,
  val soilTempCelsius: Int,
  val pestPressure: String,
  val lastInspected: String,
  val recommendedAction: String
)

enum class AlertUrgency(val label: String, val color: Color) {
  CRITICAL("Critical Threat", AgriStatusDanger),
  WARNING("Warning", AgriStatusWarning),
  ADVISORY("Advisory", AgriStatusInfo),
  SEASONAL("Good News", AgriStatusHealthy)
}

enum class AlertCategory {
  PEST,
  DISEASE,
  WEATHER,
  ADVISORY
}

data class CropAlert(
  val id: String,
  val title: String,
  val category: AlertCategory,
  val urgency: AlertUrgency,
  val affectedCrop: String,
  val dateIssued: String,
  val description: String,
  val recommendedAction: String,
  val isAcknowledged: Boolean = false
)

data class DiagnosisRecord(
  val id: String,
  val date: String,
  val crop: String,
  val plantPart: String,
  val issueName: String,
  val severity: String,
  val status: String,
  val notes: String,
  val treatmentApplied: String,
  val followUpDate: String,
  val healthScore: Int = 75,
  val confidenceScore: Int = 85,
  val diseaseRisk: String = "MODERATE"
)

data class CropHealthScore(
  val overallScore: Int = 78,
  val healthyPercentage: Int = 75,
  val diseaseSeverityScore: Int = 20,
  val pestSeverityScore: Int = 10,
  val nutrientDeficiencyScore: Int = 5,
  val confidenceScore: Int = 88,
  val mainReason: String = "Moderate early fungal lesion pressure on lower foliage."
)

data class PestAnalysis(
  val identified: Boolean = false,
  val name: String = "Unable to identify the pest confidently.",
  val category: String = "None",
  val severity: String = "low",
  val confidence: Float = 0f,
  val visibleSymptoms: List<String> = emptyList(),
  val affectedPlantPart: String = "Leaf",
  val recommendedManagement: String = "Scout undersides of leaves weekly; deploy yellow sticky traps.",
  val preventionMethods: String = "Maintain field sanitation and companion marigold planting."
)

data class NutrientDeficiencyAnalysis(
  val detected: Boolean = false,
  val suspectedDeficiency: String = "None",
  val visualEvidence: List<String> = emptyList(),
  val confidence: Float = 0f,
  val affectedPlantPart: String = "Lower foliage",
  val correctiveRecommendation: String = "Possible deficiency not evident — confirm with soil/leaf testing."
)

data class WeatherStationData(
  val locationName: String,
  val currentTempC: Int,
  val feelsLikeC: Int,
  val conditionText: String,
  val humidityPercent: Int,
  val windSpeedKmh: Int,
  val windDirection: String,
  val rainProbabilityPercent: Int,
  val uvIndex: Int,
  val soilMoisturePercent: Int,
  val soilTempC: Int,
  val sprayConditionVerdict: String,
  val sprayConditionDetail: String,
  val irrigationAdvice: String,
  val hourlyForecasts: List<HourlyWeather>,
  val dailyForecasts: List<DailyWeather>
)

data class HourlyWeather(
  val time: String,
  val tempC: Int,
  val rainChancePercent: Int,
  val iconEmoji: String
)

data class DailyWeather(
  val day: String,
  val maxTempC: Int,
  val minTempC: Int,
  val condition: String,
  val rainMm: Double,
  val iconEmoji: String
)

data class FarmProfile(
  val farmName: String = "Green Acres Farm",
  val crop: String = "Tomato",
  val cropVariety: String = "Arka Rakshak (Desi/Hybrid)",
  val farmLocation: String = "Coimbatore, Tamil Nadu",
  val areaAcreage: String = "2.5 Acres",
  val plantingDate: String = "2024-07-15",
  val currentHealthScore: Int = 78,
  val currentDiseaseRisk: String = "MODERATE",
  val lastAnalysisDate: String = "Today, 10:30 AM"
)
