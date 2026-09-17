package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleFarmData
import com.example.models.FarmZone
import com.example.models.RiskLevel
import com.example.ui.components.AgriCard
import com.example.ui.components.FarmerButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenSecondary
import com.example.ui.theme.AgriStatusDanger
import com.example.ui.theme.AgriStatusHealthy
import com.example.ui.theme.AgriStatusWarning

@Composable
fun RiskMapScreen(
  modifier: Modifier = Modifier
) {
  val zones = SampleFarmData.defaultZones
  var selectedZone by remember { mutableStateOf<FarmZone>(zones[1]) } // Default to high-risk zone B
  var inspectionSuccessMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      SectionHeader(
        title = "Farm Field Risk Map",
        subtitle = "Live zone telemetry, pest pressure & risk containment"
      )
    }

    // Visual Interactive Zone Grid Map
    item {
      AgriCard {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Map, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Field Plots & Danger Heatmap",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = AgriGreenPrimary
                )
              )
            }
            Text(
              text = "Tap a plot to inspect",
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF757575))
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 2x2 Interactive Field Plots Layout
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              ZonePlotBox(
                zone = zones[0],
                isSelected = selectedZone.id == zones[0].id,
                onClick = { selectedZone = zones[0] },
                modifier = Modifier.weight(1f)
              )
              ZonePlotBox(
                zone = zones[1],
                isSelected = selectedZone.id == zones[1].id,
                onClick = { selectedZone = zones[1] },
                modifier = Modifier.weight(1f)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              ZonePlotBox(
                zone = zones[2],
                isSelected = selectedZone.id == zones[2].id,
                onClick = { selectedZone = zones[2] },
                modifier = Modifier.weight(1f)
              )
              ZonePlotBox(
                zone = zones[3],
                isSelected = selectedZone.id == zones[3].id,
                onClick = { selectedZone = zones[3] },
                modifier = Modifier.weight(1f)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Map Legend
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            LegendItem(color = AgriStatusHealthy, text = "Low Risk (Safe)")
            LegendItem(color = AgriStatusWarning, text = "Watch (Medium)")
            LegendItem(color = AgriStatusDanger, text = "Action Required (High)")
          }
        }
      }
    }

    // Selected Zone Detailed Telemetry Card
    item {
      AgriCard(
        borderColor = selectedZone.riskLevel.color.copy(alpha = 0.6f)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = selectedZone.name,
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E281E)
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${selectedZone.crop} • ${selectedZone.acreage} Total Acres",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = AgriGreenPrimary,
                  fontWeight = FontWeight.SemiBold
                )
              )
            }

            StatusBadge(
              label = selectedZone.riskLevel.label,
              statusColor = selectedZone.riskLevel.color
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Soil & Sensor Telemetry row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF1F8F1),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Soil Moisture", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF556B2F)))
                Text(
                  text = "${selectedZone.soilMoisturePercent}%",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF1F8F1),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Root Temperature", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF556B2F)))
                Text(
                  text = "${selectedZone.soilTempCelsius}°C",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF1F8F1),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Pest Pressure", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF556B2F)))
                Text(
                  text = selectedZone.pestPressure,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (selectedZone.riskLevel == RiskLevel.HIGH) AgriStatusDanger else AgriGreenPrimary
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Active Risk Factors
          Text(
            text = "Active Risk Indicators:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF263238))
          )
          Spacer(modifier = Modifier.height(6.dp))

          selectedZone.riskFactors.forEach { factor ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (selectedZone.riskLevel == RiskLevel.HIGH) Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = selectedZone.riskLevel.color,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = factor,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF424242))
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Agronomic Recommended Action
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = selectedZone.riskLevel.color.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, selectedZone.riskLevel.color.copy(alpha = 0.25f))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Agronomist Containment Protocol:",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = selectedZone.riskLevel.color
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = selectedZone.recommendedAction,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF263238),
                  lineHeight = 18.sp
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          if (inspectionSuccessMessage != null) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFE8F5E9),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = inspectionSuccessMessage!!,
                color = AgriGreenPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          FarmerButton(
            text = "Mark ${selectedZone.name.substringBefore(" •")} as Inspected",
            icon = Icons.Default.CheckCircle,
            onClick = {
              inspectionSuccessMessage = "✓ Field inspection logged for ${selectedZone.name}. Status synced with farm station."
            },
            testTag = "mark_zone_inspected_button"
          )
        }
      }
    }

    // Regional Pest Migration Radar Warning
    item {
      AgriCard {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Regional Outbreak Radar (15km Radius)",
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
              Text(text = "🐛 Fall Armyworm Pressure", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
              Text(text = "Direction: 6km East-South-East", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF757575)))
            }
            StatusBadge(label = "Approaching", statusColor = AgriStatusDanger)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "🍄 Leaf Rust Spores", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
              Text(text = "Direction: 14km West (Downwind)", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF757575)))
            }
            StatusBadge(label = "Contained", statusColor = AgriStatusHealthy)
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun ZonePlotBox(
  zone: FarmZone,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val backgroundColor = when (zone.riskLevel) {
    RiskLevel.LOW -> Color(0xFFE8F5E9)
    RiskLevel.MODERATE -> Color(0xFFFFF8E1)
    RiskLevel.HIGH -> Color(0xFFFFEBEE)
  }

  Surface(
    modifier = modifier
      .height(115.dp)
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = if (isSelected) 3.dp else 1.dp,
        color = if (isSelected) AgriGreenPrimary else zone.riskLevel.color.copy(alpha = 0.5f),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onClick)
      .testTag("zone_plot_${zone.id.lowercase()}"),
    color = backgroundColor
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Text(
          text = zone.name.substringBefore(" •"),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E281E)
          )
        )
        Box(
          modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(zone.riskLevel.color)
        )
      }

      Column {
        Text(
          text = zone.crop,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E3D2E)
          )
        )
        Text(
          text = "${zone.acreage} ac • ${zone.soilMoisturePercent}% Moist",
          style = MaterialTheme.typography.bodySmall.copy(
            color = Color(0xFF616161),
            fontSize = 11.sp
          )
        )
      }
    }
  }
}

@Composable
private fun LegendItem(color: Color, text: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall.copy(
        fontSize = 11.sp,
        color = Color(0xFF555555),
        fontWeight = FontWeight.Medium
      )
    )
  }
}
