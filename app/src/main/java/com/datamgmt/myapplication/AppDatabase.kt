package com.datamgmt.myapplication

import android.content.Context
import androidx.room.*

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val monthlyQuotaGb: Double = 30.0,
    val currentUsageBytes: Long = 0L,
    val lastCheckTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "history_entries")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val dateLabel: String,
    val simId: String,
    val bytes: Long
)

@Entity(tableName = "simulations")
data class SimulationEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val monthlyQuotaGb: Double,
    val currentUsageGb: Double,
    val daysLeft: Int
)

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettings(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC")
    suspend fun getAll(): List<HistoryEntry>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: HistoryEntry)

    @Query("DELETE FROM history_entries WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}

@Dao
interface SimulationDao {
    @Query("SELECT * FROM simulations ORDER BY createdAt DESC")
    suspend fun getAll(): List<SimulationEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sim: SimulationEntry)
}

@Database(entities = [AppSettings::class, HistoryEntry::class, SimulationEntry::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun historyDao(): HistoryDao
    abstract fun simulationDao(): SimulationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}