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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SampleFarmData
import com.example.data.local.CropGuardDatabase
import com.example.models.DiagnosisRecord
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
fun HistoryScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val db = remember { CropGuardDatabase.getDatabase(context) }
  val savedDiagnoses by db.diagnosisDao().getAllDiagnoses().collectAsStateWithLifecycle(initialValue = emptyList())

  val defaultRecords = remember { SampleFarmData.defaultHistory }
  val roomRecords = savedDiagnoses.map { entity ->
    DiagnosisRecord(
      id = "local_${entity.id}",
      date = entity.formattedDate,
      crop = entity.cropName,
      plantPart = "Foliage / Leaf",
      issueName = entity.conditionName,
      severity = entity.severity,
      status = if (entity.conditionName.contains("Healthy", ignoreCase = true)) "Healthy" else "Diagnosed",
      notes = "Symptoms: ${entity.symptoms} | Risk: ${entity.riskLevel} | Confidence: ${entity.confidence} | Source: ${entity.sourceProvider}",
      treatmentApplied = entity.recommendation,
      followUpDate = "Check in 3 days"
    )
  }

  val combinedRecords = roomRecords + defaultRecords

  var searchQuery by remember { mutableStateOf("") }
  var selectedCropFilter by remember { mutableStateOf("ALL") }
  var activeDetailRecord by remember { mutableStateOf<DiagnosisRecord?>(null) }

  val filteredRecords = combinedRecords.filter { record ->
    val matchesSearch = searchQuery.isBlank() ||
      record.crop.contains(searchQuery, ignoreCase = true) ||
      record.issueName.contains(searchQuery, ignoreCase = true) ||
      record.notes.contains(searchQuery, ignoreCase = true)

    val matchesFilter = selectedCropFilter == "ALL" ||
      record.crop.startsWith(selectedCropFilter, ignoreCase = true)

    matchesSearch && matchesFilter
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      SectionHeader(
        title = "Crop Diagnosis History",
        subtitle = "Chronological archive of scouting scans and treatment logs"
      )
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by crop, disease, or symptom...") },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = AgriGreenPrimary)
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear search")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("history_search_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = AgriGreenPrimary,
          unfocusedBorderColor = Color(0xFFCFD8DC),
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        singleLine = true
      )
    }

    // Crop Filter Chips
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val cropFilters = listOf(
          "ALL" to "All Crops",
          "Tomato" to "🍅 Tomato",
          "Corn" to "🌽 Corn",
          "Rice" to "🌱 Rice",
          "Potato" to "🥔 Potato"
        )

        cropFilters.forEach { (key, label) ->
          val isSelected = selectedCropFilter == key
          FilterChip(
            selected = isSelected,
            onClick = { selectedCropFilter = key },
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
            modifier = Modifier.testTag("history_filter_${key.lowercase()}")
          )
        }
      }
    }

    // List of Diagnosis Records
    if (filteredRecords.isEmpty()) {
      item {
        AgriCard {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No diagnosis records found",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray)
            )
            Text(
              text = "Try clearing the search or changing the crop filter",
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9E9E9E))
            )
          }
        }
      }
    } else {
      items(filteredRecords, key = { it.id }) { record ->
        AgriCard(
          onClick = { activeDetailRecord = record }
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${record.crop} • ${record.plantPart}",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                  )
                )
              }

              StatusBadge(
                label = record.status,
                statusColor = when (record.status) {
                  "Healthy" -> AgriStatusHealthy
                  "Resolved" -> AgriGreenSecondary
                  "Treating" -> AgriStatusWarning
                  else -> AgriStatusDanger
                }
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = record.issueName,
              style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E281E)
              )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Severity: ${record.severity}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF555555),
                fontWeight = FontWeight.Medium
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = record.notes,
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF616161),
                lineHeight = 18.sp
              ),
              maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.CalendarToday,
                  contentDescription = null,
                  tint = Color(0xFF757575),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = record.date,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF757575),
                    fontSize = 11.sp
                  )
                )
              }

              Text(
                text = "View Full Report →",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = AgriGreenPrimary,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Full Record Detail Dialog
  if (activeDetailRecord != null) {
    val record = activeDetailRecord!!
    AlertDialog(
      onDismissRequest = { activeDetailRecord = null },
      title = {
        Text(
          text = "${record.crop} Inspection Report",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = AgriGreenPrimary
          )
        )
      },
      text = {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "ID: ${record.id}", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            StatusBadge(
              label = record.status,
              statusColor = when (record.status) {
                "Healthy" -> AgriStatusHealthy
                "Resolved" -> AgriGreenSecondary
                "Treating" -> AgriStatusWarning
                else -> AgriStatusDanger
              }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Diagnosed Condition:",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
          )
          Text(
            text = record.issueName,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E281E))
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Severity & Part:",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
          )
          Text(
            text = "${record.severity} • ${record.plantPart}",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF263238))
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Scouting Observations:",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
          )
          Text(
            text = record.notes,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF37474F), lineHeight = 18.sp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF1F8F1),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "Treatment Applied:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = record.treatmentApplied,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1B5E20), lineHeight = 18.sp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Next Follow-up Inspection: ${record.followUpDate}",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0288D1), fontWeight = FontWeight.Bold)
          )
        }
      },
      confirmButton = {
        FarmerButton(
          text = "Close Report",
          onClick = { activeDetailRecord = null },
          modifier = Modifier.fillMaxWidth()
        )
      }
    )
  }
}
