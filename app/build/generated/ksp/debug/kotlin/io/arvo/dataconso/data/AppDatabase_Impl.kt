package io.arvo.dataconso.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _settingsDao: Lazy<SettingsDao> = lazy {
    SettingsDao_Impl(this)
  }

  private val _historyDao: Lazy<HistoryDao> = lazy {
    HistoryDao_Impl(this)
  }

  private val _appQuotaDao: Lazy<AppQuotaDao> = lazy {
    AppQuotaDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(32,
        "ef1ad7ca3c86afe88e6c65fe06407c0c", "cd50ff2d9ff20fc7d7d31380227a31ab") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `app_settings` (`id` INTEGER NOT NULL, `monthlyWifiGb` REAL NOT NULL, `monthlyMobileGb` REAL NOT NULL, `billingCycleDay` INTEGER NOT NULL, `isHistoryInitialized` INTEGER NOT NULL, `vpnEnabled` INTEGER NOT NULL, `appFirewallEnabled` INTEGER NOT NULL, `speedEnabled` INTEGER NOT NULL, `ghostModeEnabled` INTEGER NOT NULL, `dailyLimitGb` REAL NOT NULL, `notificationsEnabled` INTEGER NOT NULL, `selectedTheme` TEXT NOT NULL, `selectedLanguage` TEXT NOT NULL, `onboardingCompleted` INTEGER NOT NULL, `lastResetDate` TEXT NOT NULL, `dataSavedMb` REAL NOT NULL, `blockedTrackersCount` INTEGER NOT NULL, `dnsProvider` TEXT NOT NULL, `isPremium` INTEGER NOT NULL, `premiumExpiryTimestamp` INTEGER NOT NULL, `temporaryPremiumExpiry` INTEGER NOT NULL, `hasRemovedAds` INTEGER NOT NULL, `lowPerformanceMode` INTEGER NOT NULL, `totalMoneySaved` REAL NOT NULL, `userEmail` TEXT, `userDisplayName` TEXT, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `history` (`timestamp` INTEGER NOT NULL, `dateLabel` TEXT NOT NULL, `simId` TEXT NOT NULL, `bytes` INTEGER NOT NULL, PRIMARY KEY(`dateLabel`, `simId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `simulations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `limitGb` REAL NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `app_quotas` (`packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `quotaBytes` INTEGER NOT NULL, `usedBytes` INTEGER NOT NULL, `isEnabled` INTEGER NOT NULL, `isBlocked` INTEGER NOT NULL, `isManualBlocked` INTEGER NOT NULL, `lastResetTime` INTEGER NOT NULL, `networkType` TEXT NOT NULL, PRIMARY KEY(`packageName`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ef1ad7ca3c86afe88e6c65fe06407c0c')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `app_settings`")
        connection.execSQL("DROP TABLE IF EXISTS `history`")
        connection.execSQL("DROP TABLE IF EXISTS `simulations`")
        connection.execSQL("DROP TABLE IF EXISTS `app_quotas`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsAppSettings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAppSettings.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("monthlyWifiGb", TableInfo.Column("monthlyWifiGb", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("monthlyMobileGb", TableInfo.Column("monthlyMobileGb", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("billingCycleDay", TableInfo.Column("billingCycleDay", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("isHistoryInitialized", TableInfo.Column("isHistoryInitialized",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("vpnEnabled", TableInfo.Column("vpnEnabled", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("appFirewallEnabled", TableInfo.Column("appFirewallEnabled",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("speedEnabled", TableInfo.Column("speedEnabled", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("ghostModeEnabled", TableInfo.Column("ghostModeEnabled", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("dailyLimitGb", TableInfo.Column("dailyLimitGb", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("notificationsEnabled", TableInfo.Column("notificationsEnabled",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("selectedTheme", TableInfo.Column("selectedTheme", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("selectedLanguage", TableInfo.Column("selectedLanguage", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("onboardingCompleted", TableInfo.Column("onboardingCompleted",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("lastResetDate", TableInfo.Column("lastResetDate", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("dataSavedMb", TableInfo.Column("dataSavedMb", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("blockedTrackersCount", TableInfo.Column("blockedTrackersCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("dnsProvider", TableInfo.Column("dnsProvider", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("isPremium", TableInfo.Column("isPremium", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("premiumExpiryTimestamp", TableInfo.Column("premiumExpiryTimestamp",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("temporaryPremiumExpiry", TableInfo.Column("temporaryPremiumExpiry",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("hasRemovedAds", TableInfo.Column("hasRemovedAds", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("lowPerformanceMode", TableInfo.Column("lowPerformanceMode",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("totalMoneySaved", TableInfo.Column("totalMoneySaved", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("userEmail", TableInfo.Column("userEmail", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("userDisplayName", TableInfo.Column("userDisplayName", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAppSettings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAppSettings: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAppSettings: TableInfo = TableInfo("app_settings", _columnsAppSettings,
            _foreignKeysAppSettings, _indicesAppSettings)
        val _existingAppSettings: TableInfo = read(connection, "app_settings")
        if (!_infoAppSettings.equals(_existingAppSettings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |app_settings(io.arvo.dataconso.data.AppSettings).
              | Expected:
              |""".trimMargin() + _infoAppSettings + """
              |
              | Found:
              |""".trimMargin() + _existingAppSettings)
        }
        val _columnsHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHistory.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("dateLabel", TableInfo.Column("dateLabel", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("simId", TableInfo.Column("simId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("bytes", TableInfo.Column("bytes", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHistory: TableInfo = TableInfo("history", _columnsHistory, _foreignKeysHistory,
            _indicesHistory)
        val _existingHistory: TableInfo = read(connection, "history")
        if (!_infoHistory.equals(_existingHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |history(io.arvo.dataconso.data.HistoryEntry).
              | Expected:
              |""".trimMargin() + _infoHistory + """
              |
              | Found:
              |""".trimMargin() + _existingHistory)
        }
        val _columnsSimulations: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSimulations.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSimulations.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSimulations.put("limitGb", TableInfo.Column("limitGb", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSimulations: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSimulations: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSimulations: TableInfo = TableInfo("simulations", _columnsSimulations,
            _foreignKeysSimulations, _indicesSimulations)
        val _existingSimulations: TableInfo = read(connection, "simulations")
        if (!_infoSimulations.equals(_existingSimulations)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |simulations(io.arvo.dataconso.data.SimulationEntry).
              | Expected:
              |""".trimMargin() + _infoSimulations + """
              |
              | Found:
              |""".trimMargin() + _existingSimulations)
        }
        val _columnsAppQuotas: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAppQuotas.put("packageName", TableInfo.Column("packageName", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("appName", TableInfo.Column("appName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("quotaBytes", TableInfo.Column("quotaBytes", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("usedBytes", TableInfo.Column("usedBytes", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("isEnabled", TableInfo.Column("isEnabled", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("isBlocked", TableInfo.Column("isBlocked", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("isManualBlocked", TableInfo.Column("isManualBlocked", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("lastResetTime", TableInfo.Column("lastResetTime", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAppQuotas.put("networkType", TableInfo.Column("networkType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAppQuotas: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAppQuotas: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAppQuotas: TableInfo = TableInfo("app_quotas", _columnsAppQuotas,
            _foreignKeysAppQuotas, _indicesAppQuotas)
        val _existingAppQuotas: TableInfo = read(connection, "app_quotas")
        if (!_infoAppQuotas.equals(_existingAppQuotas)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |app_quotas(io.arvo.dataconso.data.AppQuotaEntity).
              | Expected:
              |""".trimMargin() + _infoAppQuotas + """
              |
              | Found:
              |""".trimMargin() + _existingAppQuotas)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "app_settings", "history",
        "simulations", "app_quotas")
  }

  public override fun clearAllTables() {
    super.performClear(false, "app_settings", "history", "simulations", "app_quotas")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(SettingsDao::class, SettingsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HistoryDao::class, HistoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AppQuotaDao::class, AppQuotaDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun settingsDao(): SettingsDao = _settingsDao.value

  public override fun historyDao(): HistoryDao = _historyDao.value

  public override fun quotaDao(): AppQuotaDao = _appQuotaDao.value
}
