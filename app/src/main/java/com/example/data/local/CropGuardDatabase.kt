package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DiagnosisEntity::class], version = 1, exportSchema = false)
abstract class CropGuardDatabase : RoomDatabase() {
  abstract fun diagnosisDao(): DiagnosisDao

  companion object {
    @Volatile
    private var INSTANCE: CropGuardDatabase? = null

    fun getDatabase(context: Context): CropGuardDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CropGuardDatabase::class.java,
          "cropguard_offline_db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
