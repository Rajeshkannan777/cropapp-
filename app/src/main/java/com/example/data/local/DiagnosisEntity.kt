package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnoses")
data class DiagnosisEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val formattedDate: String,
  val cropName: String,
  val conditionName: String,
  val category: String,
  val confidence: String,
  val severity: String,
  val riskLevel: String,
  val symptoms: String,
  val recommendation: String,
  val language: String,
  val sourceProvider: String
)
