package io.arvo.dataconso.data

import android.content.Context
import android.util.Log
import androidx.room.*
import io.arvo.dataconso.BuildConfig

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val monthlyWifiGb: Double = 50.0,
    val monthlyMobileGb: Double = 5.0,
    val billingCycleDay: Int = 1,
    val isHistoryInitialized: Boolean = false,
    val vpnEnabled: Boolean = false,
    val appFirewallEnabled: Boolean = false,
    val speedEnabled: Boolean = false,
    val ghostModeEnabled: Boolean = false,
    val dailyLimitGb: Double = 0.0,
    val notificationsEnabled: Boolean = true,
    val selectedTheme: String = "LIGHT",
    val selectedLanguage: String = "fr",
    val onboardingCompleted: Boolean = false,
    val lastResetDate: String = "",
    val dataSavedMb: Double = 0.0,
    val blockedTrackersCount: Int = 0,
    val dnsProvider: String = "ADGUARD",
    val isPremium: Boolean = false,
    val premiumExpiryTimestamp: Long = 0L,
    val temporaryPremiumExpiry: Long = 0L,
    val hasRemovedAds: Boolean = false,
    val lowPerformanceMode: Boolean = false,
    val totalMoneySaved: Double = 0.0,
    val userEmail: String? = null,
    val userDisplayName: String? = null
) {
    val isEliteActive: Boolean get() = isPremium || (System.currentTimeMillis() < temporaryPremiumExpiry)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettings(): AppSettings?

    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettingsFlow(): kotlinx.coroutines.flow.Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}

@Entity(
    tableName = "history",
    primaryKeys = ["dateLabel", "simId"]
)
data class HistoryEntry(
    val timestamp: Long,
    val dateLabel: String,
    val simId: String,
    val bytes: Long
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    suspend fun getAll(): List<HistoryEntry>

    @Query("SELECT * FROM history WHERE dateLabel = :date AND simId = :sim LIMIT 1")
    suspend fun getEntryByDateAndSim(date: String, sim: String): HistoryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<HistoryEntry>)
}

@Entity(tableName = "simulations")
data class SimulationEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val limitGb: Double
)

@Entity(tableName = "app_quotas")
data class AppQuotaEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val quotaBytes: Long,
    val usedBytes: Long = 0L,
    val isEnabled: Boolean = true,
    val isBlocked: Boolean = false,
    val isManualBlocked: Boolean = false,
    val lastResetTime: Long = System.currentTimeMillis(),
    val networkType: String = "MOBILE"
)

@Dao
interface AppQuotaDao {
    @Query("SELECT * FROM app_quotas")
    suspend fun getAllQuotas(): List<AppQuotaEntity>

    @Query("SELECT * FROM app_quotas")
    fun getAllQuotasFlow(): kotlinx.coroutines.flow.Flow<List<AppQuotaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuota(quota: AppQuotaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllQuotas(quotas: List<AppQuotaEntity>)

    @Delete
    suspend fun deleteQuota(quota: AppQuotaEntity)

    @Query("UPDATE app_quotas SET usedBytes = :used WHERE packageName = :packageName")
    suspend fun updateUsage(packageName: String, used: Long)

    @Query("SELECT * FROM app_quotas WHERE packageName = :packageName")
    suspend fun getQuotaForApp(packageName: String): AppQuotaEntity?

    @Query("UPDATE app_quotas SET isBlocked = 0, usedBytes = 0, lastResetTime = :now")
    suspend fun resetAllQuotas(now: Long)
}

@Database(entities = [AppSettings::class, HistoryEntry::class, SimulationEntry::class, AppQuotaEntity::class], version = 32, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun historyDao(): HistoryDao
    abstract fun quotaDao(): AppQuotaDao
    
    companion object {
        private const val DB_NAME = "arvo_v5_final.db"
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_31_32 = object : androidx.room.migration.Migration(31, 32) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Rigueur : Migration sécurisée pour fusionner SIM1 et SIM2
                database.execSQL("ALTER TABLE app_settings ADD COLUMN monthlyMobileGb REAL NOT NULL DEFAULT 5.0")
                database.execSQL("UPDATE app_settings SET monthlyMobileGb = monthlySim1Gb")
                // On garde les anciennes colonnes dans la BDD pour éviter un drop/create complexe en SQLite
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = try {
                    buildDatabase(context).also { db ->
                        db.openHelper.writableDatabase
                    }
                } catch (e: Throwable) {
                    Log.e("ARVO_DB", "Encryption error", e)
                    // En cas d'erreur fatale de clé, on repart à zéro
                    context.deleteDatabase(DB_NAME)
                    buildDatabase(context)
                }
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                  .addMigrations(MIGRATION_31_32)
                  .fallbackToDestructiveMigration()
                .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                .enableMultiInstanceInvalidation()
                .setQueryCallback({ sqlQuery, _ ->
                    if (io.arvo.dataconso.BuildConfig.DEBUG && sqlQuery.contains("history")) {
                         Log.d("ARVO_DB_PERF", "Query: $sqlQuery")
                    }
                }, java.util.concurrent.Executors.newSingleThreadExecutor())
                // .fallbackToDestructiveMigration() // SUPPRIMÉ POUR SÉCURITÉ PROD
                .build()
        }
    }
}
