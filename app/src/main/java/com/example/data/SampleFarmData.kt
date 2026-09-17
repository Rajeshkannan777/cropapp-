package com.example.data

import com.example.models.AlertCategory
import com.example.models.AlertUrgency
import com.example.models.CropAlert
import com.example.models.DailyWeather
import com.example.models.DiagnosisRecord
import com.example.models.FarmZone
import com.example.models.HourlyWeather
import com.example.models.RiskLevel
import com.example.models.WeatherStationData

object SampleFarmData {

  val defaultWeather = WeatherStationData(
    locationName = "Green Valley Station • Field 4",
    currentTempC = 25,
    feelsLikeC = 26,
    conditionText = "Partly Cloudy • Calm Wind",
    humidityPercent = 62,
    windSpeedKmh = 7,
    windDirection = "NW",
    rainProbabilityPercent = 15,
    uvIndex = 6,
    soilMoisturePercent = 44,
    soilTempC = 21,
    sprayConditionVerdict = "Excellent Spray Window",
    sprayConditionDetail = "Wind < 10 km/h and no heavy rainfall expected in the next 12 hours. Leaf absorption optimal.",
    irrigationAdvice = "Soil moisture is in the healthy zone (44%). Hold standard irrigation for 18 hours.",
    hourlyForecasts = listOf(
      HourlyWeather("Now", 25, 10, "⛅"),
      HourlyWeather("10 AM", 27, 10, "🌤️"),
      HourlyWeather("12 PM", 29, 15, "☀️"),
      HourlyWeather("02 PM", 30, 20, "☀️"),
      HourlyWeather("04 PM", 28, 25, "⛅"),
      HourlyWeather("06 PM", 26, 15, "🌤️"),
      HourlyWeather("08 PM", 23, 10, "🌙"),
      HourlyWeather("10 PM", 21, 5, "✨")
    ),
    dailyForecasts = listOf(
      DailyWeather("Today", 30, 19, "Partly Sunny", 0.0, "⛅"),
      DailyWeather("Wed", 31, 20, "Sunny & Warm", 0.0, "☀️"),
      DailyWeather("Thu", 27, 18, "Scattered Showers", 8.4, "🌦️"),
      DailyWeather("Fri", 25, 17, "Moderate Rain", 14.2, "🌧️"),
      DailyWeather("Sat", 26, 18, "Clear Skies", 1.0, "🌤️"),
      DailyWeather("Sun", 28, 19, "Mild Breeze", 0.0, "☀️"),
      DailyWeather("Mon", 29, 20, "Mostly Sunny", 0.0, "⛅")
    )
  )

  val defaultZones = listOf(
    FarmZone(
      id = "ZONE-A",
      name = "Zone A • North Wheat Field",
      crop = "Durum Wheat",
      acreage = 18.5,
      riskLevel = RiskLevel.LOW,
      riskFactors = listOf("Canopy density normal", "Rust spore count negligible"),
      soilMoisturePercent = 48,
      soilTempCelsius = 20,
      pestPressure = "Low",
      lastInspected = "Yesterday 4:00 PM",
      recommendedAction = "Maintain standard scouting routine. No fungicide required."
    ),
    FarmZone(
      id = "ZONE-B",
      name = "Zone B • Tomato Polyhouse 2",
      crop = "Roma Tomatoes",
      acreage = 4.2,
      riskLevel = RiskLevel.HIGH,
      riskFactors = listOf("High relative humidity (88%)", "Early Blight spore cluster spotted nearby"),
      soilMoisturePercent = 58,
      soilTempCelsius = 23,
      pestPressure = "Moderate (Whitefly)",
      lastInspected = "Today 8:30 AM",
      recommendedAction = "Increase greenhouse ventilation immediately. Apply preventive bio-fungicide or copper sulfate."
    ),
    FarmZone(
      id = "ZONE-C",
      name = "Zone C • Sweetcorn Plot",
      crop = "Hybrid Sweetcorn",
      acreage = 12.0,
      riskLevel = RiskLevel.MODERATE,
      riskFactors = listOf("Fall Armyworm migration trap active within 6km", "Leaf whorl scouting advised"),
      soilMoisturePercent = 40,
      soilTempCelsius = 22,
      pestPressure = "Elevated",
      lastInspected = "2 days ago",
      recommendedAction = "Inspect lower leaf whorls for egg masses. Deploy pheromone traps on perimeter."
    ),
    FarmZone(
      id = "ZONE-D",
      name = "Zone D • Apple Orchard Ridge",
      crop = "Honeycrisp Apples",
      acreage = 9.0,
      riskLevel = RiskLevel.LOW,
      riskFactors = listOf("Scab risk low", "Fruit cluster thinning complete"),
      soilMoisturePercent = 45,
      soilTempCelsius = 19,
      pestPressure = "Minimal",
      lastInspected = "3 days ago",
      recommendedAction = "Continue codling moth trap monitoring."
    )
  )

  val defaultAlerts = listOf(
    CropAlert(
      id = "ALT-101",
      title = "Fall Armyworm Regional Infestation Alert",
      category = AlertCategory.PEST,
      urgency = AlertUrgency.CRITICAL,
      affectedCrop = "Corn / Maize, Sorghum",
      dateIssued = "Today, 07:15 AM",
      description = "Agricultural extension service has confirmed active Fall Armyworm caterpillars across 3 nearby farms (within 6km radius). High risk of foliage perforation.",
      recommendedAction = "Conduct visual whorl inspection on 20 consecutive plants across plot. Check for sawdust-like frass."
    ),
    CropAlert(
      id = "ALT-102",
      title = "Fungal Mildew & Late Blight Risk Advisory",
      category = AlertCategory.DISEASE,
      urgency = AlertUrgency.WARNING,
      affectedCrop = "Tomato, Potato",
      dateIssued = "Yesterday, 04:30 PM",
      description = "Extended leaf wetness expected Thursday evening due to incoming humidity front. Spore germination index is at 78%.",
      recommendedAction = "Ensure crop drip lines are checked to minimize leaf splash. Prepare copper-based protective shield."
    ),
    CropAlert(
      id = "ALT-103",
      title = "Prime Nutrient & Spray Application Window",
      category = AlertCategory.ADVISORY,
      urgency = AlertUrgency.ADVISORY,
      affectedCrop = "All Crops",
      dateIssued = "Today, 06:00 AM",
      description = "Winds under 8 km/h and comfortable temperatures forecasted through tomorrow morning. Minimal drift risk.",
      recommendedAction = "Schedule foliar nutrition and organic repellent applications between 06:30 AM and 10:30 AM."
    ),
    CropAlert(
      id = "ALT-104",
      title = "Soil Moisture in Optimal Vigor Range",
      category = AlertCategory.WEATHER,
      urgency = AlertUrgency.SEASONAL,
      affectedCrop = "Durum Wheat",
      dateIssued = "Sep 14, 09:00 AM",
      description = "Root zone moisture sensors report 48% saturation, providing ideal conditions for grain development without waterlogging.",
      recommendedAction = "Irrigation pumps can stay in eco-standby mode until Saturday."
    )
  )

  val defaultHistory = listOf(
    DiagnosisRecord(
      id = "DIAG-2026-089",
      date = "Sep 14, 2026 • 09:15 AM",
      crop = "Tomato",
      plantPart = "Leaf",
      issueName = "Early Blight (Alternaria solani)",
      severity = "Moderate (15% canopy affected)",
      status = "Treating",
      notes = "Concentric dark brown rings observed on lower mature leaves with chlorotic yellow halos. Weather conditions favored spread.",
      treatmentApplied = "Applied organic copper fungicide spray and pruned bottom 6 inches of foliage to reduce soil bounce-back.",
      followUpDate = "Sep 18, 2026"
    ),
    DiagnosisRecord(
      id = "DIAG-2026-088",
      date = "Sep 12, 2026 • 03:40 PM",
      crop = "Corn / Maize",
      plantPart = "Stem / Stalk",
      issueName = "Common Rust (Puccinia sorghi)",
      severity = "Mild (5% leaf area)",
      status = "Monitoring",
      notes = "Small cinnamon-brown pustules found scattered on upper leaf surface. Plant vigor remains high.",
      treatmentApplied = "Marked monitoring grid. No chemical intervention required as resistant hybrid trait is holding.",
      followUpDate = "Sep 19, 2026"
    ),
    DiagnosisRecord(
      id = "DIAG-2026-085",
      date = "Sep 08, 2026 • 11:20 AM",
      crop = "Paddy Rice",
      plantPart = "Leaf",
      issueName = "Brown Plant Hopper damage",
      severity = "Mild",
      status = "Resolved",
      notes = "Hoppers detected near water line. Prompt field drainage applied for 48 hours to disrupt reproduction cycle.",
      treatmentApplied = "Intermittent field drying plus beneficial predator spider population conserved.",
      followUpDate = "Sep 15, 2026"
    ),
    DiagnosisRecord(
      id = "DIAG-2026-081",
      date = "Sep 02, 2026 • 08:00 AM",
      crop = "Potato",
      plantPart = "Leaf",
      issueName = "Healthy Leaf Canopy",
      severity = "None",
      status = "Healthy",
      notes = "Routine scouting image taken. High chlorophyll index, clean margins, zero pest puncture marks.",
      treatmentApplied = "Routine organic seaweed fertilizer maintenance.",
      followUpDate = "Sep 16, 2026"
    )
  )
}
