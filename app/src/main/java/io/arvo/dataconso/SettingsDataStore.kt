package io.arvo.dataconso.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "arvo_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val MONTHLY_WIFI_GB = doublePreferencesKey("monthly_wifi_gb")
        val MONTHLY_MOBILE_GB = doublePreferencesKey("monthly_mobile_gb")
        val BILLING_CYCLE_DAY = intPreferencesKey("billing_cycle_day")
        val IS_HISTORY_INITIALIZED = booleanPreferencesKey("is_history_initialized")
        val VPN_ENABLED = booleanPreferencesKey("vpn_enabled")
        val APP_FIREWALL_ENABLED = booleanPreferencesKey("app_firewall_enabled")
        val SPEED_ENABLED = booleanPreferencesKey("speed_enabled")
        val GHOST_MODE_ENABLED = booleanPreferencesKey("ghost_mode_enabled")
        val DAILY_LIMIT_GB = doublePreferencesKey("daily_limit_gb")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        val DATA_SAVED_MB = doublePreferencesKey("data_saved_mb")
        val BLOCKED_TRACKERS_COUNT = intPreferencesKey("blocked_trackers_count")
        val DNS_PROVIDER = stringPreferencesKey("dns_provider")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val PREMIUM_EXPIRY_TIMESTAMP = longPreferencesKey("premium_expiry_timestamp")
        val HAS_REMOVED_ADS = booleanPreferencesKey("has_removed_ads")
        val MIGRATION_COMPLETED = booleanPreferencesKey("migration_completed")
        val LOW_PERFORMANCE_MODE = booleanPreferencesKey("low_performance_mode")
        val TEMPORARY_PREMIUM_EXPIRY = longPreferencesKey("temporary_premium_expiry")
        val TOTAL_MONEY_SAVED = doublePreferencesKey("total_money_saved")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_DISPLAY_NAME = stringPreferencesKey("user_display_name")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun updateSettings(newSettings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MONTHLY_WIFI_GB] = newSettings.monthlyWifiGb
            preferences[PreferencesKeys.MONTHLY_MOBILE_GB] = newSettings.monthlyMobileGb
            preferences[PreferencesKeys.BILLING_CYCLE_DAY] = newSettings.billingCycleDay
            preferences[PreferencesKeys.IS_HISTORY_INITIALIZED] = newSettings.isHistoryInitialized
            preferences[PreferencesKeys.VPN_ENABLED] = newSettings.vpnEnabled
            preferences[PreferencesKeys.APP_FIREWALL_ENABLED] = newSettings.appFirewallEnabled
            preferences[PreferencesKeys.SPEED_ENABLED] = newSettings.speedEnabled
            preferences[PreferencesKeys.GHOST_MODE_ENABLED] = newSettings.ghostModeEnabled
            preferences[PreferencesKeys.DAILY_LIMIT_GB] = newSettings.dailyLimitGb
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = newSettings.notificationsEnabled
            preferences[PreferencesKeys.SELECTED_THEME] = newSettings.selectedTheme
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = newSettings.selectedLanguage
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = newSettings.onboardingCompleted
            preferences[PreferencesKeys.LAST_RESET_DATE] = newSettings.lastResetDate
            preferences[PreferencesKeys.DATA_SAVED_MB] = newSettings.dataSavedMb
            preferences[PreferencesKeys.BLOCKED_TRACKERS_COUNT] = newSettings.blockedTrackersCount
            preferences[PreferencesKeys.DNS_PROVIDER] = newSettings.dnsProvider
            preferences[PreferencesKeys.IS_PREMIUM] = newSettings.isPremium
            preferences[PreferencesKeys.PREMIUM_EXPIRY_TIMESTAMP] = newSettings.premiumExpiryTimestamp
            preferences[PreferencesKeys.HAS_REMOVED_ADS] = newSettings.hasRemovedAds
            preferences[PreferencesKeys.LOW_PERFORMANCE_MODE] = newSettings.lowPerformanceMode
            preferences[PreferencesKeys.TEMPORARY_PREMIUM_EXPIRY] = newSettings.temporaryPremiumExpiry
            preferences[PreferencesKeys.TOTAL_MONEY_SAVED] = newSettings.totalMoneySaved
            newSettings.userEmail?.let { preferences[PreferencesKeys.USER_EMAIL] = it }
            newSettings.userDisplayName?.let { preferences[PreferencesKeys.USER_DISPLAY_NAME] = it }
        }
    }

    private fun mapUserPreferences(preferences: Preferences): AppSettings {
        return AppSettings(
            monthlyWifiGb = preferences[PreferencesKeys.MONTHLY_WIFI_GB] ?: 50.0,
            monthlyMobileGb = preferences[PreferencesKeys.MONTHLY_MOBILE_GB] ?: 5.0,
            billingCycleDay = preferences[PreferencesKeys.BILLING_CYCLE_DAY] ?: 1,
            isHistoryInitialized = preferences[PreferencesKeys.IS_HISTORY_INITIALIZED] ?: false,
            vpnEnabled = preferences[PreferencesKeys.VPN_ENABLED] ?: false,
            appFirewallEnabled = preferences[PreferencesKeys.APP_FIREWALL_ENABLED] ?: false,
            speedEnabled = preferences[PreferencesKeys.SPEED_ENABLED] ?: false,
            ghostModeEnabled = preferences[PreferencesKeys.GHOST_MODE_ENABLED] ?: false,
            dailyLimitGb = preferences[PreferencesKeys.DAILY_LIMIT_GB] ?: 2.0,
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            selectedTheme = preferences[PreferencesKeys.SELECTED_THEME] ?: "LIGHT",
            selectedLanguage = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "fr",
            onboardingCompleted = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
            lastResetDate = preferences[PreferencesKeys.LAST_RESET_DATE] ?: "",
            dataSavedMb = preferences[PreferencesKeys.DATA_SAVED_MB] ?: 0.0,
            blockedTrackersCount = preferences[PreferencesKeys.BLOCKED_TRACKERS_COUNT] ?: 0,
            dnsProvider = preferences[PreferencesKeys.DNS_PROVIDER] ?: "ADGUARD",
            isPremium = preferences[PreferencesKeys.IS_PREMIUM] ?: false,
            premiumExpiryTimestamp = preferences[PreferencesKeys.PREMIUM_EXPIRY_TIMESTAMP] ?: 0L,
            hasRemovedAds = preferences[PreferencesKeys.HAS_REMOVED_ADS] ?: false,
            lowPerformanceMode = preferences[PreferencesKeys.LOW_PERFORMANCE_MODE] ?: io.arvo.dataconso.security.SecurityUtils.isLowEndDevice(context),
            temporaryPremiumExpiry = preferences[PreferencesKeys.TEMPORARY_PREMIUM_EXPIRY] ?: 0L,
            totalMoneySaved = preferences[PreferencesKeys.TOTAL_MONEY_SAVED] ?: 0.0,
            userEmail = preferences[PreferencesKeys.USER_EMAIL],
            userDisplayName = preferences[PreferencesKeys.USER_DISPLAY_NAME]
        )
    }
    
    suspend fun isMigrationCompleted(): Boolean {
        return context.dataStore.data.map { it[PreferencesKeys.MIGRATION_COMPLETED] ?: false }.catch { emit(false) }.first()
    }
    
    suspend fun setMigrationCompleted() {
        context.dataStore.edit { it[PreferencesKeys.MIGRATION_COMPLETED] = true }
    }
}
