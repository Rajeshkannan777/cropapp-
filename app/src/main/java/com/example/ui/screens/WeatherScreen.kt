package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleFarmData
import com.example.ui.components.AgriCard
import com.example.ui.components.FarmerButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenSecondary
import com.example.ui.theme.AgriStatusHealthy
import com.example.ui.theme.AgriStatusInfo
import com.example.ui.theme.AgriStatusWarning

@Composable
fun WeatherScreen(
  modifier: Modifier = Modifier
) {
  val weather = SampleFarmData.defaultWeather
  var lastUpdated by remember { mutableStateOf("Just now") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      SectionHeader(
        title = "Agricultural Weather Station",
        subtitle = "Live microclimate & spray advisory for Field #4"
      )
    }

    // Weather Station Hero Card
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("weather_hero_card"),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              brush = Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF2E7D32),
                  Color(0xFF1B5E20)
                )
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = Color(0xFFA5D6A7),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = weather.locationName,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                  )
                )
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x33FFFFFF)
              ) {
                Text(
                  text = "Telemetry: Live",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "${weather.currentTempC}°C",
                  style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                )
                Text(
                  text = weather.conditionText,
                  style = MaterialTheme.typography.titleSmall.copy(
                    color = Color(0xFFC8E6C9),
                    fontWeight = FontWeight.Medium
                  )
                )
                Text(
                  text = "Feels like ${weather.feelsLikeC}°C • UV Index: ${weather.uvIndex}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE8F5E9)
                  )
                )
              }

              Text(
                text = "⛅",
                fontSize = 58.sp
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Agricultural metrics strip
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              WeatherMetricPill(
                icon = Icons.Default.Opacity,
                label = "Humidity",
                value = "${weather.humidityPercent}%"
              )
              WeatherMetricPill(
                icon = Icons.Default.Air,
                label = "Wind",
                value = "${weather.windSpeedKmh} km/h ${weather.windDirection}"
              )
              WeatherMetricPill(
                icon = Icons.Default.WaterDrop,
                label = "Rain Risk",
                value = "${weather.rainProbabilityPercent}%"
              )
              WeatherMetricPill(
                icon = Icons.Default.Thermostat,
                label = "Soil Temp",
                value = "${weather.soilTempC}°C"
              )
            }
          }
        }
      }
    }

    // Farmer Spraying Window Advisory Card
    item {
      AgriCard(borderColor = AgriGreenPrimary.copy(alpha = 0.4f)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8F1))
            .padding(16.dp),
          verticalAlignment = Alignment.Top
        ) {
          Surface(
            shape = CircleShape,
            color = AgriGreenPrimary,
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = weather.sprayConditionVerdict,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = AgriGreenPrimary
                )
              )
              StatusBadge(label = "SAFE TO SPRAY", statusColor = AgriStatusHealthy)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = weather.sprayConditionDetail,
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF37474F),
                lineHeight = 18.sp
              )
            )
          }
        }
      }
    }

    // Irrigation & Soil Health Card
    item {
      AgriCard {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Irrigation & Root Zone Moisture",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E281E)
            )
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Volumetric Soil Moisture",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF616161))
              )
              Text(
                text = "${weather.soilMoisturePercent}% (Optimal 35-50%)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = AgriGreenSecondary
                )
              )
            }
            StatusBadge(label = "Optimal Hydration", statusColor = AgriStatusHealthy)
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = weather.irrigationAdvice,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF455A64),
              lineHeight = 18.sp
            )
          )
        }
      }
    }

    // Hourly Forecast Scroll Strip
    item {
      AgriCard {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Hourly Field Conditions (Next 12 Hours)",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E281E)
            )
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            weather.hourlyForecasts.forEach { hourly ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF7FAF5),
                modifier = Modifier.width(68.dp)
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = hourly.time,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF616161)
                    )
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(text = hourly.iconEmoji, fontSize = 22.sp)
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "${hourly.tempC}°",
                    style = MaterialTheme.typography.labelLarge.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF212121)
                    )
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "${hourly.rainChancePercent}% rain",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 10.sp,
                      color = Color(0xFF0288D1),
                      fontWeight = FontWeight.SemiBold
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // 7-Day Agricultural Forecast
    item {
      AgriCard {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "7-Day Farm Outlook & Precipitation",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E281E)
            )
          )
          Spacer(modifier = Modifier.height(12.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            weather.dailyForecasts.forEach { day ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.width(90.dp)
                ) {
                  Text(text = day.iconEmoji, fontSize = 18.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = day.day,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF263238)
                    )
                  )
                }

                Text(
                  text = day.condition,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF546E7A)
                  ),
                  modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (day.rainMm > 0) {
                    Text(
                      text = "${day.rainMm} mm",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF0288D1),
                        fontWeight = FontWeight.Bold
                      )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                  } else {
                    Text(
                      text = "0 mm",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF9E9E9E)
                      )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                  }

                  Text(
                    text = "${day.maxTempC}° / ${day.minTempC}°",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1E281E)
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // Refresh Sensor Readings Action
    item {
      FarmerButton(
        text = "Refresh Field Telemetry",
        icon = Icons.Default.Refresh,
        onClick = { lastUpdated = "Updated just now" },
        testTag = "refresh_weather_button"
      )
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun WeatherMetricPill(
  icon: ImageVector,
  label: String,
  value: String
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Color(0xFFA5D6A7),
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = value,
      color = Color.White,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    )
    Text(
      text = label,
      color = Color(0xFFC8E6C9),
      fontSize = 10.sp
    )
  }
}
