package io.arvo.dataconso.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SettingsDao_Impl(
  __db: RoomDatabase,
) : SettingsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAppSettings: EntityInsertAdapter<AppSettings>
  init {
    this.__db = __db
    this.__insertAdapterOfAppSettings = object : EntityInsertAdapter<AppSettings>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `app_settings` (`id`,`monthlyWifiGb`,`monthlyMobileGb`,`billingCycleDay`,`isHistoryInitialized`,`vpnEnabled`,`appFirewallEnabled`,`speedEnabled`,`ghostModeEnabled`,`dailyLimitGb`,`notificationsEnabled`,`selectedTheme`,`selectedLanguage`,`onboardingCompleted`,`lastResetDate`,`dataSavedMb`,`blockedTrackersCount`,`dnsProvider`,`isPremium`,`premiumExpiryTimestamp`,`temporaryPremiumExpiry`,`hasRemovedAds`,`lowPerformanceMode`,`totalMoneySaved`,`userEmail`,`userDisplayName`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AppSettings) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindDouble(2, entity.monthlyWifiGb)
        statement.bindDouble(3, entity.monthlyMobileGb)
        statement.bindLong(4, entity.billingCycleDay.toLong())
        val _tmp: Int = if (entity.isHistoryInitialized) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmp_1: Int = if (entity.vpnEnabled) 1 else 0
        statement.bindLong(6, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.appFirewallEnabled) 1 else 0
        statement.bindLong(7, _tmp_2.toLong())
        val _tmp_3: Int = if (entity.speedEnabled) 1 else 0
        statement.bindLong(8, _tmp_3.toLong())
        val _tmp_4: Int = if (entity.ghostModeEnabled) 1 else 0
        statement.bindLong(9, _tmp_4.toLong())
        statement.bindDouble(10, entity.dailyLimitGb)
        val _tmp_5: Int = if (entity.notificationsEnabled) 1 else 0
        statement.bindLong(11, _tmp_5.toLong())
        statement.bindText(12, entity.selectedTheme)
        statement.bindText(13, entity.selectedLanguage)
        val _tmp_6: Int = if (entity.onboardingCompleted) 1 else 0
        statement.bindLong(14, _tmp_6.toLong())
        statement.bindText(15, entity.lastResetDate)
        statement.bindDouble(16, entity.dataSavedMb)
        statement.bindLong(17, entity.blockedTrackersCount.toLong())
        statement.bindText(18, entity.dnsProvider)
        val _tmp_7: Int = if (entity.isPremium) 1 else 0
        statement.bindLong(19, _tmp_7.toLong())
        statement.bindLong(20, entity.premiumExpiryTimestamp)
        statement.bindLong(21, entity.temporaryPremiumExpiry)
        val _tmp_8: Int = if (entity.hasRemovedAds) 1 else 0
        statement.bindLong(22, _tmp_8.toLong())
        val _tmp_9: Int = if (entity.lowPerformanceMode) 1 else 0
        statement.bindLong(23, _tmp_9.toLong())
        statement.bindDouble(24, entity.totalMoneySaved)
        val _tmpUserEmail: String? = entity.userEmail
        if (_tmpUserEmail == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpUserEmail)
        }
        val _tmpUserDisplayName: String? = entity.userDisplayName
        if (_tmpUserDisplayName == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpUserDisplayName)
        }
      }
    }
  }

  public override suspend fun saveSettings(settings: AppSettings): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfAppSettings.insert(_connection, settings)
  }

  public override suspend fun getSettings(): AppSettings? {
    val _sql: String = "SELECT * FROM app_settings WHERE id = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMonthlyWifiGb: Int = getColumnIndexOrThrow(_stmt, "monthlyWifiGb")
        val _columnIndexOfMonthlyMobileGb: Int = getColumnIndexOrThrow(_stmt, "monthlyMobileGb")
        val _columnIndexOfBillingCycleDay: Int = getColumnIndexOrThrow(_stmt, "billingCycleDay")
        val _columnIndexOfIsHistoryInitialized: Int = getColumnIndexOrThrow(_stmt,
            "isHistoryInitialized")
        val _columnIndexOfVpnEnabled: Int = getColumnIndexOrThrow(_stmt, "vpnEnabled")
        val _columnIndexOfAppFirewallEnabled: Int = getColumnIndexOrThrow(_stmt,
            "appFirewallEnabled")
        val _columnIndexOfSpeedEnabled: Int = getColumnIndexOrThrow(_stmt, "speedEnabled")
        val _columnIndexOfGhostModeEnabled: Int = getColumnIndexOrThrow(_stmt, "ghostModeEnabled")
        val _columnIndexOfDailyLimitGb: Int = getColumnIndexOrThrow(_stmt, "dailyLimitGb")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfSelectedTheme: Int = getColumnIndexOrThrow(_stmt, "selectedTheme")
        val _columnIndexOfSelectedLanguage: Int = getColumnIndexOrThrow(_stmt, "selectedLanguage")
        val _columnIndexOfOnboardingCompleted: Int = getColumnIndexOrThrow(_stmt,
            "onboardingCompleted")
        val _columnIndexOfLastResetDate: Int = getColumnIndexOrThrow(_stmt, "lastResetDate")
        val _columnIndexOfDataSavedMb: Int = getColumnIndexOrThrow(_stmt, "dataSavedMb")
        val _columnIndexOfBlockedTrackersCount: Int = getColumnIndexOrThrow(_stmt,
            "blockedTrackersCount")
        val _columnIndexOfDnsProvider: Int = getColumnIndexOrThrow(_stmt, "dnsProvider")
        val _columnIndexOfIsPremium: Int = getColumnIndexOrThrow(_stmt, "isPremium")
        val _columnIndexOfPremiumExpiryTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "premiumExpiryTimestamp")
        val _columnIndexOfTemporaryPremiumExpiry: Int = getColumnIndexOrThrow(_stmt,
            "temporaryPremiumExpiry")
        val _columnIndexOfHasRemovedAds: Int = getColumnIndexOrThrow(_stmt, "hasRemovedAds")
        val _columnIndexOfLowPerformanceMode: Int = getColumnIndexOrThrow(_stmt,
            "lowPerformanceMode")
        val _columnIndexOfTotalMoneySaved: Int = getColumnIndexOrThrow(_stmt, "totalMoneySaved")
        val _columnIndexOfUserEmail: Int = getColumnIndexOrThrow(_stmt, "userEmail")
        val _columnIndexOfUserDisplayName: Int = getColumnIndexOrThrow(_stmt, "userDisplayName")
        val _result: AppSettings?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpMonthlyWifiGb: Double
          _tmpMonthlyWifiGb = _stmt.getDouble(_columnIndexOfMonthlyWifiGb)
          val _tmpMonthlyMobileGb: Double
          _tmpMonthlyMobileGb = _stmt.getDouble(_columnIndexOfMonthlyMobileGb)
          val _tmpBillingCycleDay: Int
          _tmpBillingCycleDay = _stmt.getLong(_columnIndexOfBillingCycleDay).toInt()
          val _tmpIsHistoryInitialized: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsHistoryInitialized).toInt()
          _tmpIsHistoryInitialized = _tmp != 0
          val _tmpVpnEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVpnEnabled).toInt()
          _tmpVpnEnabled = _tmp_1 != 0
          val _tmpAppFirewallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfAppFirewallEnabled).toInt()
          _tmpAppFirewallEnabled = _tmp_2 != 0
          val _tmpSpeedEnabled: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfSpeedEnabled).toInt()
          _tmpSpeedEnabled = _tmp_3 != 0
          val _tmpGhostModeEnabled: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfGhostModeEnabled).toInt()
          _tmpGhostModeEnabled = _tmp_4 != 0
          val _tmpDailyLimitGb: Double
          _tmpDailyLimitGb = _stmt.getDouble(_columnIndexOfDailyLimitGb)
          val _tmpNotificationsEnabled: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_5 != 0
          val _tmpSelectedTheme: String
          _tmpSelectedTheme = _stmt.getText(_columnIndexOfSelectedTheme)
          val _tmpSelectedLanguage: String
          _tmpSelectedLanguage = _stmt.getText(_columnIndexOfSelectedLanguage)
          val _tmpOnboardingCompleted: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfOnboardingCompleted).toInt()
          _tmpOnboardingCompleted = _tmp_6 != 0
          val _tmpLastResetDate: String
          _tmpLastResetDate = _stmt.getText(_columnIndexOfLastResetDate)
          val _tmpDataSavedMb: Double
          _tmpDataSavedMb = _stmt.getDouble(_columnIndexOfDataSavedMb)
          val _tmpBlockedTrackersCount: Int
          _tmpBlockedTrackersCount = _stmt.getLong(_columnIndexOfBlockedTrackersCount).toInt()
          val _tmpDnsProvider: String
          _tmpDnsProvider = _stmt.getText(_columnIndexOfDnsProvider)
          val _tmpIsPremium: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsPremium).toInt()
          _tmpIsPremium = _tmp_7 != 0
          val _tmpPremiumExpiryTimestamp: Long
          _tmpPremiumExpiryTimestamp = _stmt.getLong(_columnIndexOfPremiumExpiryTimestamp)
          val _tmpTemporaryPremiumExpiry: Long
          _tmpTemporaryPremiumExpiry = _stmt.getLong(_columnIndexOfTemporaryPremiumExpiry)
          val _tmpHasRemovedAds: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfHasRemovedAds).toInt()
          _tmpHasRemovedAds = _tmp_8 != 0
          val _tmpLowPerformanceMode: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfLowPerformanceMode).toInt()
          _tmpLowPerformanceMode = _tmp_9 != 0
          val _tmpTotalMoneySaved: Double
          _tmpTotalMoneySaved = _stmt.getDouble(_columnIndexOfTotalMoneySaved)
          val _tmpUserEmail: String?
          if (_stmt.isNull(_columnIndexOfUserEmail)) {
            _tmpUserEmail = null
          } else {
            _tmpUserEmail = _stmt.getText(_columnIndexOfUserEmail)
          }
          val _tmpUserDisplayName: String?
          if (_stmt.isNull(_columnIndexOfUserDisplayName)) {
            _tmpUserDisplayName = null
          } else {
            _tmpUserDisplayName = _stmt.getText(_columnIndexOfUserDisplayName)
          }
          _result =
              AppSettings(_tmpId,_tmpMonthlyWifiGb,_tmpMonthlyMobileGb,_tmpBillingCycleDay,_tmpIsHistoryInitialized,_tmpVpnEnabled,_tmpAppFirewallEnabled,_tmpSpeedEnabled,_tmpGhostModeEnabled,_tmpDailyLimitGb,_tmpNotificationsEnabled,_tmpSelectedTheme,_tmpSelectedLanguage,_tmpOnboardingCompleted,_tmpLastResetDate,_tmpDataSavedMb,_tmpBlockedTrackersCount,_tmpDnsProvider,_tmpIsPremium,_tmpPremiumExpiryTimestamp,_tmpTemporaryPremiumExpiry,_tmpHasRemovedAds,_tmpLowPerformanceMode,_tmpTotalMoneySaved,_tmpUserEmail,_tmpUserDisplayName)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSettingsFlow(): Flow<AppSettings?> {
    val _sql: String = "SELECT * FROM app_settings WHERE id = 1"
    return createFlow(__db, false, arrayOf("app_settings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMonthlyWifiGb: Int = getColumnIndexOrThrow(_stmt, "monthlyWifiGb")
        val _columnIndexOfMonthlyMobileGb: Int = getColumnIndexOrThrow(_stmt, "monthlyMobileGb")
        val _columnIndexOfBillingCycleDay: Int = getColumnIndexOrThrow(_stmt, "billingCycleDay")
        val _columnIndexOfIsHistoryInitialized: Int = getColumnIndexOrThrow(_stmt,
            "isHistoryInitialized")
        val _columnIndexOfVpnEnabled: Int = getColumnIndexOrThrow(_stmt, "vpnEnabled")
        val _columnIndexOfAppFirewallEnabled: Int = getColumnIndexOrThrow(_stmt,
            "appFirewallEnabled")
        val _columnIndexOfSpeedEnabled: Int = getColumnIndexOrThrow(_stmt, "speedEnabled")
        val _columnIndexOfGhostModeEnabled: Int = getColumnIndexOrThrow(_stmt, "ghostModeEnabled")
        val _columnIndexOfDailyLimitGb: Int = getColumnIndexOrThrow(_stmt, "dailyLimitGb")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfSelectedTheme: Int = getColumnIndexOrThrow(_stmt, "selectedTheme")
        val _columnIndexOfSelectedLanguage: Int = getColumnIndexOrThrow(_stmt, "selectedLanguage")
        val _columnIndexOfOnboardingCompleted: Int = getColumnIndexOrThrow(_stmt,
            "onboardingCompleted")
        val _columnIndexOfLastResetDate: Int = getColumnIndexOrThrow(_stmt, "lastResetDate")
        val _columnIndexOfDataSavedMb: Int = getColumnIndexOrThrow(_stmt, "dataSavedMb")
        val _columnIndexOfBlockedTrackersCount: Int = getColumnIndexOrThrow(_stmt,
            "blockedTrackersCount")
        val _columnIndexOfDnsProvider: Int = getColumnIndexOrThrow(_stmt, "dnsProvider")
        val _columnIndexOfIsPremium: Int = getColumnIndexOrThrow(_stmt, "isPremium")
        val _columnIndexOfPremiumExpiryTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "premiumExpiryTimestamp")
        val _columnIndexOfTemporaryPremiumExpiry: Int = getColumnIndexOrThrow(_stmt,
            "temporaryPremiumExpiry")
        val _columnIndexOfHasRemovedAds: Int = getColumnIndexOrThrow(_stmt, "hasRemovedAds")
        val _columnIndexOfLowPerformanceMode: Int = getColumnIndexOrThrow(_stmt,
            "lowPerformanceMode")
        val _columnIndexOfTotalMoneySaved: Int = getColumnIndexOrThrow(_stmt, "totalMoneySaved")
        val _columnIndexOfUserEmail: Int = getColumnIndexOrThrow(_stmt, "userEmail")
        val _columnIndexOfUserDisplayName: Int = getColumnIndexOrThrow(_stmt, "userDisplayName")
        val _result: AppSettings?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpMonthlyWifiGb: Double
          _tmpMonthlyWifiGb = _stmt.getDouble(_columnIndexOfMonthlyWifiGb)
          val _tmpMonthlyMobileGb: Double
          _tmpMonthlyMobileGb = _stmt.getDouble(_columnIndexOfMonthlyMobileGb)
          val _tmpBillingCycleDay: Int
          _tmpBillingCycleDay = _stmt.getLong(_columnIndexOfBillingCycleDay).toInt()
          val _tmpIsHistoryInitialized: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsHistoryInitialized).toInt()
          _tmpIsHistoryInitialized = _tmp != 0
          val _tmpVpnEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVpnEnabled).toInt()
          _tmpVpnEnabled = _tmp_1 != 0
          val _tmpAppFirewallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfAppFirewallEnabled).toInt()
          _tmpAppFirewallEnabled = _tmp_2 != 0
          val _tmpSpeedEnabled: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfSpeedEnabled).toInt()
          _tmpSpeedEnabled = _tmp_3 != 0
          val _tmpGhostModeEnabled: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfGhostModeEnabled).toInt()
          _tmpGhostModeEnabled = _tmp_4 != 0
          val _tmpDailyLimitGb: Double
          _tmpDailyLimitGb = _stmt.getDouble(_columnIndexOfDailyLimitGb)
          val _tmpNotificationsEnabled: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_5 != 0
          val _tmpSelectedTheme: String
          _tmpSelectedTheme = _stmt.getText(_columnIndexOfSelectedTheme)
          val _tmpSelectedLanguage: String
          _tmpSelectedLanguage = _stmt.getText(_columnIndexOfSelectedLanguage)
          val _tmpOnboardingCompleted: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfOnboardingCompleted).toInt()
          _tmpOnboardingCompleted = _tmp_6 != 0
          val _tmpLastResetDate: String
          _tmpLastResetDate = _stmt.getText(_columnIndexOfLastResetDate)
          val _tmpDataSavedMb: Double
          _tmpDataSavedMb = _stmt.getDouble(_columnIndexOfDataSavedMb)
          val _tmpBlockedTrackersCount: Int
          _tmpBlockedTrackersCount = _stmt.getLong(_columnIndexOfBlockedTrackersCount).toInt()
          val _tmpDnsProvider: String
          _tmpDnsProvider = _stmt.getText(_columnIndexOfDnsProvider)
          val _tmpIsPremium: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsPremium).toInt()
          _tmpIsPremium = _tmp_7 != 0
          val _tmpPremiumExpiryTimestamp: Long
          _tmpPremiumExpiryTimestamp = _stmt.getLong(_columnIndexOfPremiumExpiryTimestamp)
          val _tmpTemporaryPremiumExpiry: Long
          _tmpTemporaryPremiumExpiry = _stmt.getLong(_columnIndexOfTemporaryPremiumExpiry)
          val _tmpHasRemovedAds: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfHasRemovedAds).toInt()
          _tmpHasRemovedAds = _tmp_8 != 0
          val _tmpLowPerformanceMode: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfLowPerformanceMode).toInt()
          _tmpLowPerformanceMode = _tmp_9 != 0
          val _tmpTotalMoneySaved: Double
          _tmpTotalMoneySaved = _stmt.getDouble(_columnIndexOfTotalMoneySaved)
          val _tmpUserEmail: String?
          if (_stmt.isNull(_columnIndexOfUserEmail)) {
            _tmpUserEmail = null
          } else {
            _tmpUserEmail = _stmt.getText(_columnIndexOfUserEmail)
          }
          val _tmpUserDisplayName: String?
          if (_stmt.isNull(_columnIndexOfUserDisplayName)) {
            _tmpUserDisplayName = null
          } else {
            _tmpUserDisplayName = _stmt.getText(_columnIndexOfUserDisplayName)
          }
          _result =
              AppSettings(_tmpId,_tmpMonthlyWifiGb,_tmpMonthlyMobileGb,_tmpBillingCycleDay,_tmpIsHistoryInitialized,_tmpVpnEnabled,_tmpAppFirewallEnabled,_tmpSpeedEnabled,_tmpGhostModeEnabled,_tmpDailyLimitGb,_tmpNotificationsEnabled,_tmpSelectedTheme,_tmpSelectedLanguage,_tmpOnboardingCompleted,_tmpLastResetDate,_tmpDataSavedMb,_tmpBlockedTrackersCount,_tmpDnsProvider,_tmpIsPremium,_tmpPremiumExpiryTimestamp,_tmpTemporaryPremiumExpiry,_tmpHasRemovedAds,_tmpLowPerformanceMode,_tmpTotalMoneySaved,_tmpUserEmail,_tmpUserDisplayName)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
