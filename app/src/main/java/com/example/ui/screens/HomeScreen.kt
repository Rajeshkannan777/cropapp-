package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.VoiceIntent
import com.example.data.SampleFarmData
import com.example.models.AppSection
import com.example.models.RiskLevel
import com.example.ui.components.AgriCard
import com.example.ui.components.FarmerButton
import com.example.ui.components.FarmerOutlinedButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.VoiceAssistantDialog
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenSecondary
import com.example.ui.theme.AgriStatusDanger
import com.example.ui.theme.AgriStatusHealthy
import com.example.ui.theme.AgriStatusWarning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  onNavigateTo: (AppSection) -> Unit,
  modifier: Modifier = Modifier
) {
  val weather = SampleFarmData.defaultWeather
  val zones = SampleFarmData.defaultZones
  val recentDiagnoses = SampleFarmData.defaultHistory.take(2)
  val alerts = SampleFarmData.defaultAlerts
  var showVoiceDialog by remember { mutableStateOf(false) }

  if (showVoiceDialog) {
    VoiceAssistantDialog(
      onDismiss = { showVoiceDialog = false },
      onIntentAction = { intent ->
        showVoiceDialog = false
        when (intent) {
          is VoiceIntent.OpenWeather -> onNavigateTo(AppSection.WEATHER)
          is VoiceIntent.OpenMap -> onNavigateTo(AppSection.RISK_MAP)
          is VoiceIntent.StartDetection, is VoiceIntent.SelectCrop -> onNavigateTo(AppSection.DETECTION)
          is VoiceIntent.ShowAlerts, is VoiceIntent.ReadAlerts -> onNavigateTo(AppSection.ALERTS)
          is VoiceIntent.ShowHistory -> onNavigateTo(AppSection.HISTORY)
          else -> {}
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      // Farmer Hero Banner with Field Vigor Metric
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_hero_banner"),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              brush = Brush.horizontalGradient(
                colors = listOf(
                  AgriGreenPrimary,
                  Color(0xFF2E7D32),
                  Color(0xFF388E3C)
                )
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "FARM HEALTH OVERVIEW",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFFA5D6A7),
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                  )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "94% Healthy Canopy",
                  style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                  )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "43.7 Active Acres Monitored across 4 Zones",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE8F5E9)
                  )
                )
              }

              Surface(
                shape = CircleShape,
                color = Color(0x33FFFFFF),
                modifier = Modifier.size(54.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Weather & Risk Pill row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x22FFFFFF),
                modifier = Modifier
                  .weight(1f)
                  .clickable { onNavigateTo(AppSection.WEATHER) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "⛅", fontSize = 18.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = "${weather.currentTempC}°C • Optimal",
                      color = Color.White,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "Good to spray",
                      color = Color(0xFFC8E6C9),
                      fontSize = 10.sp
                    )
                  }
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x22FFFFFF),
                modifier = Modifier
                  .weight(1f)
                  .clickable { onNavigateTo(AppSection.ALERTS) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "⚠️", fontSize = 18.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = "2 Field Alerts",
                      color = Color.White,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "1 High Severity",
                      color = Color(0xFFFFCC80),
                      fontSize = 10.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Large Farmer Primary Actions
    item {
      SectionHeader(
        title = "Quick Field Actions",
        subtitle = "Tap to inspect crops, weather, or farm map"
      )

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FarmerButton(
          text = "Start AI Crop Detection",
          icon = Icons.Default.AddAPhoto,
          onClick = { onNavigateTo(AppSection.DETECTION) },
          testTag = "home_start_scan_button"
        )

        FarmerButton(
          text = "🎤 Voice Assistant",
          icon = Icons.Default.Mic,
          onClick = { showVoiceDialog = true },
          containerColor = Color(0xFF2E7D32),
          testTag = "home_voice_assistant_button"
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          FarmerOutlinedButton(
            text = "Risk Map",
            icon = Icons.Default.Map,
            onClick = { onNavigateTo(AppSection.RISK_MAP) },
            modifier = Modifier.weight(1f),
            testTag = "home_risk_map_button"
          )
          FarmerOutlinedButton(
            text = "Weather",
            icon = Icons.Default.Cloud,
            onClick = { onNavigateTo(AppSection.WEATHER) },
            modifier = Modifier.weight(1f),
            testTag = "home_weather_button"
          )
        }
      }
    }

    // High Priority Field Alert Banner
    item {
      val criticalAlert = alerts.firstOrNull { it.urgency == com.example.models.AlertUrgency.CRITICAL }
      if (criticalAlert != null) {
        AgriCard(
          borderColor = AgriStatusDanger.copy(alpha = 0.5f),
          onClick = { onNavigateTo(AppSection.ALERTS) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFFFF4F2))
              .padding(14.dp),
            verticalAlignment = Alignment.Top
          ) {
            Surface(
              shape = CircleShape,
              color = AgriStatusDanger,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(
                  label = "URGENT PEST WATCH",
                  statusColor = AgriStatusDanger
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Within 6km",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AgriStatusDanger,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = criticalAlert.title,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E281E)
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Affecting: ${criticalAlert.affectedCrop}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF6B7280)
                )
              )
            }
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "View Alert",
              tint = AgriStatusDanger,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // Farm Risk Zones Snapshot
    item {
      SectionHeader(
        title = "Farm Field Zones",
        subtitle = "Live status of cultivated acreage",
        actionText = "See All 4 Zones",
        onActionClick = { onNavigateTo(AppSection.RISK_MAP) }
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        zones.take(3).forEach { zone ->
          AgriCard(
            onClick = { onNavigateTo(AppSection.RISK_MAP) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = zone.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF191C19)
                    )
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${zone.crop} • ${zone.acreage} Acres • Moisture: ${zone.soilMoisturePercent}%",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5A6658)
                  )
                )
              }

              StatusBadge(
                label = zone.riskLevel.label,
                statusColor = zone.riskLevel.color
              )
            }
          }
        }
      }
    }

    // Recent Crop Diagnoses
    item {
      SectionHeader(
        title = "Recent Diagnoses",
        subtitle = "Latest leaf inspections and treatments",
        actionText = "Full History",
        onActionClick = { onNavigateTo(AppSection.HISTORY) }
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        recentDiagnoses.forEach { record ->
          AgriCard(
            onClick = { onNavigateTo(AppSection.HISTORY) }
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${record.crop} (${record.plantPart})",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                  )
                )
                StatusBadge(
                  label = record.status,
                  statusColor = when (record.status) {
                    "Healthy" -> AgriStatusHealthy
                    "Treating" -> AgriStatusWarning
                    "Resolved" -> AgriGreenSecondary
                    else -> AgriStatusDanger
                  }
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = record.issueName,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF1E281E)
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Scanned on ${record.date}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF757575)
                )
              )
            }
          }
        }
      }
    }

    // Agronomist Daily Guidance Tip
    item {
      AgriCard(borderColor = Color(0xFFC8E6C9)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8F1))
            .padding(16.dp),
          verticalAlignment = Alignment.Top
        ) {
          Text(text = "💡", fontSize = 24.sp)
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Farmer Best Practice of the Day",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "For optimal leaf disease detection, take photos during early morning or late afternoon under indirect natural light. Avoid direct harsh midday shadows on leaf margins.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF37474F),
                lineHeight = 18.sp
              )
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
