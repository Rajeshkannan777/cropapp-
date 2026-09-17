package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
  @Query("SELECT * FROM diagnoses ORDER BY timestamp DESC")
  fun getAllDiagnoses(): Flow<List<DiagnosisEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDiagnosis(diagnosis: DiagnosisEntity): Long

  @Query("DELETE FROM diagnoses WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM diagnoses")
  suspend fun clearAll()
}
