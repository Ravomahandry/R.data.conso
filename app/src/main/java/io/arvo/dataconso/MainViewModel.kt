package io.arvo.dataconso

import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import io.arvo.dataconso.data.AppQuotaEntity
import io.arvo.dataconso.data.AppSettings
import io.arvo.dataconso.data.DataRepository
import io.arvo.dataconso.data.HistoryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar // Explicit import for Calendar
import java.util.Date // Explicit import for Date
import java.util.Locale // Explicit import for Locale

import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.net.ConnectivityManager // Required for networkType in loadDailyAppUsage

data class DailyAppUsage(
    val packageName: String,
    val appName: String,
    val appIcon: android.graphics.drawable.Drawable?,
    val usageBytes: Long,
    val networkType: NetworkSource
)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DataRepository,
    private val telephonyRepository: TelephonyRepository,
    private val aiEngine: ArvoAiEngine,
    private val usageManager: DataUsageManager,
    private val appManager: AppListManager,
    private val cloudSync: io.arvo.dataconso.backend.CloudSyncManager,
    private val getAppQuotasUseCase: io.arvo.dataconso.domain.usecase.GetAppQuotasUseCase,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : androidx.lifecycle.ViewModel() {

    private val _currentSource = MutableStateFlow(NetworkSource.MOBILE)
    val currentSource = _currentSource.asStateFlow()

    private val _currentGranularity = MutableStateFlow(Granularity.DAILY)
    val currentGranularity = _currentGranularity.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppTheme.LIGHT)
    val currentTheme = _currentTheme.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings = _settings.asStateFlow()

    private val _allInstalledApps = MutableStateFlow<List<AppUsageInfo>>(emptyList())
    private val _hasVpnConflict = MutableStateFlow(false)

    private val _dailyAppUsage = MutableStateFlow<List<DailyAppUsage>>(emptyList())
    val dailyAppUsage = _dailyAppUsage.asStateFlow()

    // 1. FAST STATE : Pour la jauge et la vitesse (Zéro Latence)
    private val _realtimeDataFlow = combine(
        RealTimeData.todayWifiBytes, // 0
        RealTimeData.todayMobileBytes, // 1
        RealTimeData.weekWifiBytes, // 2
        RealTimeData.weekMobileBytes, // 3
        RealTimeData.monthWifiBytes, // 4
        RealTimeData.monthMobileBytes, // 5
        RealTimeData.dlSpeed, // 6
        RealTimeData.ulSpeed, // 7
        RealTimeData.isVpnRunning, // 8
        RealTimeData.isTunnelActive // 9
    ) { args: Array<Any?> ->
        args
    }

    val realtimeState = combine(
        _currentSource,
        _currentGranularity,
        repository.settingsFlow
            .onStart { emit(repository.getSettings()) }
            .catch { e -> 
                Log.e("MainViewModel", "Settings flow error", e)
                emit(AppSettings()) 
            },
        _realtimeDataFlow.catch { e ->
            Log.e("MainViewModel", "Realtime data flow error", e)
            emit(arrayOf<Any?>(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, false, false))
        }
    ) { source, gran, settings, rtArgs ->
        try {
            val wifiT = rtArgs[0] as Long
            val mobileT = rtArgs[1] as Long
            val wifiW = rtArgs[2] as Long
            val mobileW = rtArgs[3] as Long
            val wifiM = rtArgs[4] as Long
            val mobileM = rtArgs[5] as Long
            val dl = rtArgs[6] as Long
            val ul = rtArgs[7] as Long
            val vpnR = rtArgs[8] as Boolean
            val tunnelA = rtArgs[9] as Boolean

            val todayBytes = if (source == NetworkSource.WIFI) wifiT else if (source == NetworkSource.MOBILE) mobileT else wifiT + mobileT
            val weekBytes = if (source == NetworkSource.WIFI) wifiW else if (source == NetworkSource.MOBILE) mobileW else wifiW + mobileW
            val monthBytes = if (source == NetworkSource.WIFI) wifiM else if (source == NetworkSource.MOBILE) mobileM else wifiM + mobileM
            
            val displayBytes = when(gran) {
                Granularity.DAILY -> todayBytes
                Granularity.WEEKLY -> weekBytes
                Granularity.MONTHLY -> monthBytes
            }

            val budget = when(source) {
                NetworkSource.WIFI -> settings.monthlyWifiGb
                NetworkSource.MOBILE -> settings.monthlyMobileGb
                NetworkSource.TOTAL -> settings.monthlyMobileGb + settings.monthlyWifiGb
            }.coerceAtLeast(0.1)

            val idealDaily = QuotaCalculator.calculateIdealDaily(budget, settings.billingCycleDay)
            val proj = QuotaCalculator.calculateProjection(budget, monthBytes / 1073741824.0, settings.billingCycleDay)
            
            val dailyQuota = if (settings.dailyLimitGb > 0 && source != NetworkSource.WIFI) settings.dailyLimitGb else proj.recommendedDailyGb
            
            val displayDaily = if (settings.dailyLimitGb > 0 && source != NetworkSource.WIFI) {
                settings.dailyLimitGb
            } else {
                if (proj.recommendedDailyGb > 0) proj.recommendedDailyGb else idealDaily
            }
            
            val displayQuota = when(gran) {
                Granularity.DAILY -> displayDaily
                Granularity.WEEKLY -> displayDaily * 7
                Granularity.MONTHLY -> budget
            }

            RealtimeState(
                dailyQuotaGb = dailyQuota,
                displayQuotaGb = displayQuota,
                todayUsedGb = todayBytes / 1073741824.0,
                weekUsedGb = weekBytes / 1073741824.0,
                monthUsedGb = monthBytes / 1073741824.0,
                displayUsedGb = displayBytes / 1073741824.0,
                surplusGb = QuotaCalculator.calculateAccumulatedSurplus(budget, monthBytes / 1073741824.0, settings.billingCycleDay),
                dlSpeed = dl,
                ulSpeed = ul,
                isVpnRunning = vpnR,
                isTunnelActive = tunnelA
            )
        } catch (e: Exception) {
            Log.e("MainViewModel", "Realtime state computation error", e)
            RealtimeState()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RealtimeState())

    // 2. SLOW STATE : Pour le graphique et les apps (Rafraîchi périodiquement)
    private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    private val _topApps = MutableStateFlow<List<AppUsageInfo>>(emptyList())
    private val _insights = MutableStateFlow<List<ArvoInsight>>(emptyList())
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val calculatedQuotasFlow: Flow<List<AppQuotaEntity>> = combine(
        RealTimeData.appUsagesWifi,
        RealTimeData.appUsagesMobile
    ) { wifiMap, mobileMap ->
        wifiMap to mobileMap
    }.flatMapLatest { (wifiMap, mobileMap) ->
        getAppQuotasUseCase(wifiMap, mobileMap)
    }

    val analysisState = combine(
        _history, 
        _topApps, 
        _insights, 
        calculatedQuotasFlow.onStart { emit(emptyList()) },
        _currentSource,
        RealTimeData.todayWifiBytes,
        RealTimeData.todayMobileBytes
    ) { args: Array<Any?> ->
        val h = args[0] as List<HistoryEntry>
        val t = args[1] as List<AppUsageInfo>
        val i = args[2] as List<ArvoInsight>
        val q = args[3] as List<AppQuotaEntity>
        val source = args[4] as NetworkSource
        val wifiT = args[5] as Long
        val mobileT = args[6] as Long

        // Sommité : Fusion de l'historique avec les données temps réel pour la barre "Aujourd'hui"
        val todayBytes = if (source == NetworkSource.WIFI) wifiT else if (source == NetworkSource.MOBILE) mobileT else wifiT + mobileT
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val todayLabel = sdf.format(java.util.Date())
        val todaySimId = when(source) {
            NetworkSource.WIFI -> "WIFI"
            NetworkSource.MOBILE -> "SIM_COMBINED"
            NetworkSource.TOTAL -> "TOTAL"
        }

        val mergedHistory = h.filterNot { it.dateLabel == todayLabel && it.simId == todaySimId }.toMutableList()
        if (todayBytes > 0) {
            mergedHistory.add(HistoryEntry(System.currentTimeMillis(), todayLabel, todaySimId, todayBytes))
        }
        
        AnalysisState(mergedHistory.sortedBy { it.timestamp }, t, i, q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalysisState())

    data class RealtimeState(
        val dailyQuotaGb: Double = 0.0,
        val displayQuotaGb: Double = 0.0,
        val todayUsedGb: Double = 0.0,
        val weekUsedGb: Double = 0.0,
        val monthUsedGb: Double = 0.0,
        val displayUsedGb: Double = 0.0,
        val surplusGb: Double = 0.0,
        val dlSpeed: Long = 0,
        val ulSpeed: Long = 0,
        val isVpnRunning: Boolean = false,
        val isTunnelActive: Boolean = false
    )

    data class AnalysisState(
        val history: List<HistoryEntry> = emptyList(),
        val topApps: List<AppUsageInfo> = emptyList(),
        val insights: List<ArvoInsight> = emptyList(),
        val appQuotas: List<AppQuotaEntity> = emptyList()
    )

    // Ancienne structure pour compatibilité
    val uiState = combine(
        realtimeState, 
        analysisState, 
        _currentSource,
        RealTimeData.appUsagesWifi,
        RealTimeData.appUsagesMobile,
        _allInstalledApps,
        _hasVpnConflict
    ) { args: Array<Any?> ->
        val r = args[0] as RealtimeState
        val a = args[1] as AnalysisState
        val source = args[2] as NetworkSource
        val liveWifi = args[3] as Map<String, Long>
        val liveMobile = args[4] as Map<String, Long>
        val installedApps = args[5] as List<AppUsageInfo>
        val hasVpnConflict = args[6] as Boolean

        // Sommité : Fusion des données Top Apps (Système + Temps Réel)
        val liveMap = if (source == NetworkSource.WIFI) liveWifi else if (source == NetworkSource.MOBILE) liveMobile else {
            (liveWifi.keys + liveMobile.keys).associateWith { (liveWifi[it] ?: 0L) + (liveMobile[it] ?: 0L) }
        }

        val mergedTopApps = a.topApps.map { systemApp ->
            val liveUsage = liveMap[systemApp.packageName] ?: 0L
            // Si le live est significativement plus élevé (NSM en retard), on prend le live
            if (liveUsage > systemApp.bytes) systemApp.copy(bytes = liveUsage) else systemApp
        }.toMutableList()

        // Ajouter les apps qui sont dans le live mais pas encore dans le top système
        liveMap.forEach { (pkg, bytes) ->
            if (mergedTopApps.none { it.packageName == pkg } && bytes > 1024 * 50) { // > 50 Ko
                val name = installedApps.find { it.packageName == pkg }?.appName ?: pkg.split('.').last()
                mergedTopApps.add(AppUsageInfo(pkg, name, bytes))
            }
        }

        MainViewModel.UiState(
            dailyQuotaGb = r.dailyQuotaGb,
            todayUsedGb = r.todayUsedGb,
            monthUsedGb = r.monthUsedGb,
            remainingGb = (r.dailyQuotaGb - r.todayUsedGb).coerceAtLeast(0.0),
            surplusGb = r.surplusGb,
            history = a.history,
            topApps = mergedTopApps.sortedByDescending { it.bytes }.take(5),
            insights = a.insights,
            appQuotas = a.appQuotas,
            isVpnRunning = r.isVpnRunning,
            isTunnelActive = r.isTunnelActive,
            hasVpnConflict = hasVpnConflict,
            allInstalledApps = installedApps
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainViewModel.UiState())

    data class UiState(
        val dailyQuotaGb: Double = 0.0,
        val todayUsedGb: Double = 0.0,
        val monthUsedGb: Double = 0.0,
        val remainingGb: Double = 0.0,
        val surplusGb: Double = 0.0,
        val history: List<HistoryEntry> = emptyList(),
        val topApps: List<AppUsageInfo> = emptyList(),
        val appQuotas: List<AppQuotaEntity> = emptyList(),
        val insights: List<ArvoInsight> = emptyList(),
        val isVpnRunning: Boolean = false,
        val isTunnelActive: Boolean = false,
        val hasVpnConflict: Boolean = false,
        val allInstalledApps: List<AppUsageInfo> = emptyList(),
        val hasUsageStats: Boolean = false,
        val isAuthentic: Boolean = true
    )

    val simSubscriptions = telephonyRepository.activeSubscriptions

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { s ->
                _settings.value = s
                _currentTheme.value = try { AppTheme.valueOf(s.selectedTheme) } catch (_: Throwable) { AppTheme.LIGHT }
            }
        }

        telephonyRepository.refreshSimInfo()
        
        // Keep the dashboard live even when the VPN service is not running.
        viewModelScope.launch(Dispatchers.IO) {
            var lastTrafficAt = android.os.SystemClock.elapsedRealtime()
            var lastRx = android.net.TrafficStats.getTotalRxBytes()
            var lastTx = android.net.TrafficStats.getTotalTxBytes()
            var lastUsageSyncAt = -5000L

            while (isActive) {
                val now = android.os.SystemClock.elapsedRealtime()
                val elapsedSeconds = (now - lastTrafficAt) / 1000.0
                val rx = android.net.TrafficStats.getTotalRxBytes()
                val tx = android.net.TrafficStats.getTotalTxBytes()

                if (elapsedSeconds > 0.0) {
                    RealTimeData.updateAndBroadcast(
                        context,
                        dl = ((rx - lastRx).coerceAtLeast(0L) / elapsedSeconds).toLong(),
                        ul = ((tx - lastTx).coerceAtLeast(0L) / elapsedSeconds).toLong()
                    )
                }
                lastRx = rx
                lastTx = tx
                lastTrafficAt = now

                if (now - lastUsageSyncAt >= 5000L) {
                    syncRealTimeWithSystem()
                    lastUsageSyncAt = now
                }
                delay(2000L)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                refreshHeavyData()
                _hasVpnConflict.value = isAnotherVpnActive()
                delay(5000)
            }
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            _allInstalledApps.value = appManager.getInstalledApps()
        }
    }

    private suspend fun syncRealTimeWithSystem() {
        try {
                val s = repository.getSettings()
                val activeSims = telephonyRepository.activeSubscriptions.value
                val currentSub = activeSims.find { it.isDefaultData }
                val subId = currentSub?.subscriptionId ?: -1

                val wifiToday = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.DAILY, android.net.NetworkCapabilities.TRANSPORT_WIFI).totalBytes
                val mobileToday = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.DAILY, android.net.NetworkCapabilities.TRANSPORT_CELLULAR, subId = subId).totalBytes
                
                val wifiWeek = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.WEEKLY, android.net.NetworkCapabilities.TRANSPORT_WIFI).totalBytes
                val mobileWeek = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.WEEKLY, android.net.NetworkCapabilities.TRANSPORT_CELLULAR, subId = subId).totalBytes

                val wifiMonth = usageManager.getUsageBreakdownForBillingCycle(s.billingCycleDay, android.net.NetworkCapabilities.TRANSPORT_WIFI).totalBytes
                val mobileMonth = usageManager.getUsageBreakdownForBillingCycle(s.billingCycleDay, android.net.NetworkCapabilities.TRANSPORT_CELLULAR, subId = subId).totalBytes

                Log.d("ARVO_SYNC", "Initial Sync: WifiToday=$wifiToday, MobileToday=$mobileToday, WifiWeek=$wifiWeek, MobileWeek=$mobileWeek")

                RealTimeData.updateAndBroadcast(
                    context,
                    wifiToday = wifiToday,
                    mobileToday = mobileToday,
                    wifiWeek = wifiWeek,
                    mobileWeek = mobileWeek,
                    wifiMonth = wifiMonth,
                    mobileMonth = mobileMonth
                )
        } catch (e: Exception) {
            Log.e("ARVO_VM", "Sync error", e)
        }
    }

    private fun refreshHeavyData() {
        val source = _currentSource.value
        val gran = _currentGranularity.value
        
        Log.d("ARVO_HEAVY", "Refreshing: Source=$source, Gran=$gran")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val history = repository.getAllHistory()
                _history.value = history
            } catch (e: Exception) { Log.e("ARVO_VM", "Error history", e) }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val topApps = repository.getTopApps(source, gran)
                Log.d("ARVO_HEAVY", "TopApps Count: ${topApps.size} for $source")
                _topApps.value = topApps
            } catch (e: Exception) { Log.e("ARVO_VM", "Error topApps", e) }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val insights = aiEngine.generateInsights(RealTimeData.monthMobileBytes.value)
                _insights.value = insights
            } catch (e: Exception) { Log.e("ARVO_VM", "Error insights", e) }
        }
    }

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        repository.saveSettings(_settings.value.copy(selectedTheme = theme.name))
        cloudSync.logEvent("ui_theme_changed", android.os.Bundle().apply { putString("theme", theme.name) })
    }

    fun setSource(source: NetworkSource) { 
        _currentSource.value = source
        // Rigueur Sommité : Force une synchronisation immédiate du service et des données lourdes
        refreshNow()
        cloudSync.logEvent("ui_source_changed", android.os.Bundle().apply { putString("source", source.name) })
    }
    
    fun setGranularity(granularity: Granularity) { 
        _currentGranularity.value = granularity
        // Rigueur Sommité : Force une synchronisation immédiate du service et des données lourdes
        refreshNow()
    }

    fun refreshNow() {
        viewModelScope.launch {
            kickVpnService()
            refreshHeavyData()
        }
    }

    fun addAppQuota(pkg: String, name: String, bytes: Long, netType: String) = viewModelScope.launch(Dispatchers.IO) {
        val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        repository.saveQuota(AppQuotaEntity(pkg, name, bytes, 0L, true, false, false, cal.timeInMillis, netType))
        kickVpnService()
    }

    fun updateAppQuota(quota: AppQuotaEntity, newBytes: Long, newNetType: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveQuota(
            quota.copy(
                quotaBytes = newBytes,
                networkType = newNetType,
                isBlocked = newBytes > 0L && quota.usedBytes >= newBytes
            )
        )
        kickVpnService()
    }

    fun removeAppQuota(quota: AppQuotaEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteQuota(quota)
        kickVpnService()
    }

    fun toggleManualBlock(quota: AppQuotaEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveQuota(quota.copy(isManualBlocked = !quota.isManualBlocked))
        kickVpnService()
    }

    private fun kickVpnService() {
        val prepareIntent = android.net.VpnService.prepare(context)
        if (prepareIntent != null) {
            Log.w("ARVO_VM", "VPN permission required; waiting for the visible activity to request it")
            return
        }

        val i = Intent(context, VpnBlockService::class.java).apply { action = VpnBlockService.ACTION_REFRESH }
        try {
            androidx.core.content.ContextCompat.startForegroundService(context, i)
        } catch (e: Exception) {
            Log.e("ARVO_VM", "Unable to start protection service", e)
        }
    }

    private fun isAnotherVpnActive(): Boolean {
        val cm = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE)
            as? android.net.ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN) &&
            !RealTimeData.isTunnelActive.value
    }

    fun updateSpeedStatus(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(speedEnabled = enabled))
        kickVpnService()
    }

    fun updateVpnStatus(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(vpnEnabled = enabled))
        cloudSync.logEvent("settings_vpn_toggle", android.os.Bundle().apply { putBoolean("enabled", enabled) })
        kickVpnService()
    }

    fun updateAppFirewallStatus(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(appFirewallEnabled = enabled))
        kickVpnService()
    }

    fun updateGhostModeStatus(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(ghostModeEnabled = enabled))
        kickVpnService()
    }

    fun updatePerformanceMode(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(lowPerformanceMode = enabled))
    }

    suspend fun loadDailyAppUsage(dateMillis: Long, networkSource: NetworkSource) = viewModelScope.launch {
        val installedApps = appManager.getInstalledApps()
        val dailyUsageList = mutableListOf<DailyAppUsage>()

        val networkType = when (networkSource) {
            NetworkSource.WIFI -> android.net.ConnectivityManager.TYPE_WIFI
            NetworkSource.MOBILE -> android.net.ConnectivityManager.TYPE_MOBILE
            NetworkSource.TOTAL -> -1 // Handled by summing both WIFI and MOBILE later
        }

        for (appInfo in installedApps) {
            val pm = context.packageManager
            val appIcon = try { pm.getApplicationIcon(appInfo.packageName) } catch (_: Exception) { null }

            var totalAppUsageBytes = 0L

            if (networkType != -1) {
                // Specific network type (WIFI or MOBILE)
                val usage = usageManager.getAppUsageForDay(appInfo.packageName, dateMillis, networkType)
                totalAppUsageBytes = usage.totalBytes
            } else {
                // TOTAL (sum WIFI and MOBILE)
                val wifiUsage = usageManager.getAppUsageForDay(appInfo.packageName, dateMillis, android.net.ConnectivityManager.TYPE_WIFI)
                val mobileUsage = usageManager.getAppUsageForDay(appInfo.packageName, dateMillis, android.net.ConnectivityManager.TYPE_MOBILE)
                totalAppUsageBytes = wifiUsage.totalBytes + mobileUsage.totalBytes
            }

            if (totalAppUsageBytes > 0) {
                dailyUsageList.add(
                    DailyAppUsage(
                        packageName = appInfo.packageName,
                        appName = appInfo.appName,
                        appIcon = appIcon,
                        usageBytes = totalAppUsageBytes,
                        networkType = networkSource
                    )
                )
            }
        }

        _dailyAppUsage.value = dailyUsageList.sortedByDescending { it.usageBytes }
    }

    fun updateConfig(wifi: Double, mobile: Double, cycleDay: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(monthlyWifiGb = wifi, monthlyMobileGb = mobile, billingCycleDay = cycleDay.coerceIn(1, 28)))
        kickVpnService()
    }

    fun setLanguage(lang: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(selectedLanguage = lang))
        val prefs = context.getSharedPreferences("arvo_settings", android.content.Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", lang).apply()
        withContext(Dispatchers.Main) {
            val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(lang)
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    fun setDnsProvider(provider: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(dnsProvider = provider))
        kickVpnService()
    }

    fun completeOnboarding() = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(onboardingCompleted = true))
    }

    fun exportUsageToPdf() = viewModelScope.launch(Dispatchers.IO) {
        val currentState = uiState.value
        val path = io.arvo.dataconso.util.PdfExporter.generateUsageReport(context, currentState)
        if (path != null) {
            val intent = Intent("io.arvo.dataconso.PDF_READY").apply { putExtra("path", path); setPackage(context.packageName) }
            context.sendBroadcast(intent)
        }
    }

    fun togglePremiumStatus() = viewModelScope.launch(Dispatchers.IO) {
        repository.saveSettings(_settings.value.copy(isPremium = !_settings.value.isPremium))
    }

    fun performInsightAction(actionId: String) = viewModelScope.launch(Dispatchers.IO) {
        if (actionId == "optimize_month") updateGhostModeStatus(true)
        else if (actionId.startsWith("block_")) {
            val pkg = actionId.removePrefix("block_")
            repository.getAllQuotas().find { it.packageName == pkg }?.let { toggleManualBlock(it) }
        }
    }

    fun shareSavings() {
        val message = "J'ai économisé ${String.format(Locale.US, "%.2f", _settings.value.totalMoneySaved)} € avec ARVO 2.0 !"
        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, message); flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        context.startActivity(Intent.createChooser(intent, "Partager").apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    fun signInAnonymously() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = FirebaseAuth.getInstance().signInAnonymously().await()
            result.user?.let { repository.saveSettings(_settings.value.copy(userEmail = "user_${it.uid.take(5)}@arvo.cloud")) }
        } catch (_: Exception) {}
    }
}
