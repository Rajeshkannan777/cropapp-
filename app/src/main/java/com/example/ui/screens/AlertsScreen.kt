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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleFarmData
import com.example.models.AlertCategory
import com.example.models.AlertUrgency
import com.example.models.CropAlert
import com.example.ui.components.AgriCard
import com.example.ui.components.FarmerButton
import com.example.ui.components.FarmerOutlinedButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriStatusDanger
import com.example.ui.theme.AgriStatusHealthy
import com.example.ui.theme.AgriStatusInfo
import com.example.ui.theme.AgriStatusWarning
import kotlinx.coroutines.launch

@Composable
fun AlertsScreen(
  modifier: Modifier = Modifier
) {
  val initialAlerts = remember { SampleFarmData.defaultAlerts }
  val acknowledgedIds = remember { mutableStateListOf<String>() }
  var selectedFilter by remember { mutableStateOf("ALL") }
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  val filteredAlerts = initialAlerts.filter { alert ->
    when (selectedFilter) {
      "CRITICAL" -> alert.urgency == AlertUrgency.CRITICAL
      "PESTS" -> alert.category == AlertCategory.PEST
      "DISEASES" -> alert.category == AlertCategory.DISEASE
      "WEATHER" -> alert.category == AlertCategory.WEATHER
      else -> true
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        SectionHeader(
          title = "Farm Field Alerts",
          subtitle = "Pest migrations, fungal spore warnings & agro-bulletins"
        )
      }

      // Summary Card
      item {
        AgriCard {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            AlertMetricItem(count = "1", label = "Critical Threat", color = AgriStatusDanger)
            AlertMetricItem(count = "1", label = "Warning", color = AgriStatusWarning)
            AlertMetricItem(count = "2", label = "Advisories", color = AgriStatusInfo)
          }
        }
      }

      // Filter Chips
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val filters = listOf(
            "ALL" to "All Alerts",
            "CRITICAL" to "🔴 Critical Only",
            "PESTS" to "🐛 Pests",
            "DISEASES" to "🍄 Diseases",
            "WEATHER" to "🌦️ Weather"
          )

          filters.forEach { (key, label) ->
            val isSelected = selectedFilter == key
            FilterChip(
              selected = isSelected,
              onClick = { selectedFilter = key },
              label = {
                Text(
                  text = label,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AgriGreenPrimary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.testTag("alert_filter_${key.lowercase()}")
            )
          }
        }
      }

      // Alerts List
      items(filteredAlerts, key = { it.id }) { alert ->
        val isAck = acknowledgedIds.contains(alert.id)

        AgriCard(
          borderColor = if (isAck) Color(0xFFCFD8DC) else alert.urgency.color.copy(alpha = 0.5f)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                StatusBadge(
                  label = alert.urgency.label,
                  statusColor = if (isAck) Color.Gray else alert.urgency.color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = alert.dateIssued,
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF757575))
                )
              }

              if (isAck) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFECEFF1)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF455A64), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Acknowledged", fontSize = 11.sp, color = Color(0xFF455A64))
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = alert.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isAck) Color(0xFF616161) else Color(0xFF1E281E)
              )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Target Crops: ${alert.affectedCrop}",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = alert.description,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF37474F),
                lineHeight = 20.sp
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Protocol Box
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF9FBF8),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "Farmer Action Required:",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                  )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = alert.recommendedAction,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF263238),
                    lineHeight = 18.sp
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button actions
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              if (!isAck) {
                FarmerButton(
                  text = "Acknowledge Alert",
                  onClick = {
                    acknowledgedIds.add(alert.id)
                    coroutineScope.launch {
                      snackbarHostState.showSnackbar("Alert '${alert.title.take(30)}...' marked acknowledged.")
                    }
                  },
                  modifier = Modifier.weight(1f),
                  testTag = "ack_alert_${alert.id}"
                )
              } else {
                FarmerOutlinedButton(
                  text = "Mark Unread",
                  onClick = { acknowledgedIds.remove(alert.id) },
                  modifier = Modifier.weight(1f)
                )
              }

              FarmerOutlinedButton(
                text = "Share with Crew",
                icon = Icons.Default.Share,
                onClick = {
                  coroutineScope.launch {
                    snackbarHostState.showSnackbar("Alert advisory shared with field workers via SMS/WhatsApp dispatch.")
                  }
                },
                modifier = Modifier.weight(1f),
                testTag = "share_alert_${alert.id}"
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    SnackbarHost(
      hostState = snackbarHostState,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp)
    )
  }
}

@Composable
private fun AlertMetricItem(count: String, label: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = count,
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = color
      )
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(
        color = Color(0xFF616161),
        fontWeight = FontWeight.Medium
      )
    )
  }
}
