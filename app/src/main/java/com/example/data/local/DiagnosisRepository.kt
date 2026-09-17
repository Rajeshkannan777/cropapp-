package com.example.data.local

import kotlinx.coroutines.flow.Flow

class DiagnosisRepository(private val diagnosisDao: DiagnosisDao) {
  val allDiagnoses: Flow<List<DiagnosisEntity>> = diagnosisDao.getAllDiagnoses()

  suspend fun insertDiagnosis(diagnosis: DiagnosisEntity): Long {
    return diagnosisDao.insertDiagnosis(diagnosis)
  }

  suspend fun deleteDiagnosis(id: Long) {
    diagnosisDao.deleteById(id)
  }

  suspend fun clearAll() {
    diagnosisDao.clearAll()
  }
}
