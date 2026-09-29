package io.arvo.dataconso

import android.annotation.SuppressLint
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.net.VpnService
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.arvo.dataconso.data.AppQuotaEntity
import io.arvo.dataconso.network.DnsResolver
import io.arvo.dataconso.network.RealPacketInterceptor
import io.arvo.dataconso.domain.usecase.AppQuotaPolicy
import io.arvo.dataconso.domain.usecase.AppQuotaRuntimeSnapshot
import io.arvo.dataconso.domain.usecase.VpnDiagnosticsEngine
import io.arvo.dataconso.domain.usecase.VpnDiagnosticEventType
import io.arvo.dataconso.domain.usecase.VpnRuntimeSnapshot
import io.arvo.dataconso.util.TimeUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@AndroidEntryPoint
class VpnBlockService : VpnService() {

    @Inject lateinit var repository: DataRepository
    @Inject lateinit var usageManager: DataUsageManager
    @Inject lateinit var telephonyRepository: TelephonyRepository
    @Inject lateinit var database: AppDatabase
    @Inject lateinit var packetInterceptor: RealPacketInterceptor
    @Inject lateinit var dnsResolver: DnsResolver
    @Inject lateinit var cloudSync: io.arvo.dataconso.backend.CloudSyncManager

    private var vpnInterface: ParcelFileDescriptor? = null
    private val channelId = "arvo_protection_v2"
    private val notificationId = 1011
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var monitoringJob: Job? = null
    private val quotaMutex = Mutex()

    private var isScreenOn = true
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var speedTextView: TextView? = null

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var lastPackageName: String? = null
    private var lastUnderlyingWifi = false
    private var lastUnderlyingMobile = false

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> { isScreenOn = true; startMonitoring() }
                Intent.ACTION_SCREEN_OFF -> { isScreenOn = false; startMonitoring() }
                "io.arvo.dataconso.APP_CHANGED" -> {
                    val pkg = intent.getStringExtra("pkg")
                    if (pkg != lastPackageName) {
                        lastPackageName = pkg
                        serviceScope.launch { checkQuotasAndSpeed(forceSync = false) }
                    }
                }
            }
        }
    }

    private var lastRxBytes: Long = 0
    private var lastTxBytes: Long = 0
    private var lastTimestamp: Long = 0
    private var lastDlSpeed: Long = 0
    private var lastUlSpeed: Long = 0

    companion object {
        const val ACTION_REFRESH = "io.arvo.dataconso.ACTION_REFRESH"
        private const val HOST_PACKAGE = BuildConfig.APPLICATION_ID
        private const val QUOTA_SYNC_INTERVAL_MILLIS = 2_000L
    }

    override fun onCreate() {
        super.onCreate()
        RealTimeData.updateAndBroadcast(this, isRunning = true)
        RealTimeData.updateAppQuotaRuntime(AppQuotaRuntimeSnapshot())
        interceptorStateJob = serviceScope.launch {
            packetInterceptor.state.collect { state ->
                when (state.status) {
                    RealPacketInterceptor.Status.ACTIVE -> {
                        if (vpnInterface != null) {
                            if (tunnelSinceTimestamp == null) {
                                tunnelSinceTimestamp = System.currentTimeMillis()
                            }
                            RealTimeData.updateAndBroadcast(this@VpnBlockService, isTunnelActive = true)
                            RealTimeData.updateAppQuotaRuntime(
                                AppQuotaRuntimeSnapshot(
                                    appliedPackages = requestedQuotaPackages - failedQuotaPackages,
                                    failedPackages = failedQuotaPackages,
                                    globalBlocked = requestedGlobalBlock,
                                    errorMessage = ruleErrorMessage
                                )
                            )
                            updateVpnRuntimeSnapshot(
                                blockRequired = requestedGlobalBlock || requestedQuotaPackages.isNotEmpty(),
                                rebuilding = false,
                                tunnelActive = true
                            )
                        }
                    }
                    RealPacketInterceptor.Status.ERROR -> {
                        val message = state.errorMessage ?: getString(R.string.blocking_error_unknown)
                        ruleErrorMessage = message
                        recordVpnError(message)
                        RealTimeData.updateAndBroadcast(this@VpnBlockService, isTunnelActive = false)
                        RealTimeData.updateAppQuotaRuntime(
                            AppQuotaRuntimeSnapshot(
                                globalBlocked = requestedGlobalBlock,
                                failedPackages = requestedQuotaPackages,
                                errorMessage = message
                            )
                        )
                        updateVpnRuntimeSnapshot(
                            blockRequired = requestedGlobalBlock || requestedQuotaPackages.isNotEmpty(),
                            rebuilding = false,
                            tunnelActive = false,
                            errorMessage = message
                        )
                    }
                    RealPacketInterceptor.Status.IDLE -> Unit
                    RealPacketInterceptor.Status.STARTING -> Unit
                }
            }
        }
        serviceScope.launch { dnsResolver.loadBlockList() }
        val filter = IntentFilter().apply { 
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction("io.arvo.dataconso.APP_CHANGED")
        }
        ContextCompat.registerReceiver(
            this,
            screenReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        registerNetworkCallback()
    }

    override fun onDestroy() {
        RealTimeData.updateAndBroadcast(this, isRunning = false)
        monitoringJob?.cancel()
        interceptorStateJob?.cancel()
        unregisterNetworkCallback()
        stopVpn()
        hideFloatingWindow()
        
        try { 
            unregisterReceiver(screenReceiver) 
        } catch (e: IllegalArgumentException) {
            Log.d("VpnBlockService", "Receiver wasn't registered", e)
        } catch (e: Exception) {
            Log.e("VpnBlockService", "Error unregistering receiver", e)
        }
        
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun registerNetworkCallback() {
        val cm = getSystemService(ConnectivityManager::class.java) ?: return
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                serviceScope.launch { checkQuotasAndSpeed(forceSync = false) }
            }
            override fun onLost(network: Network) {
                serviceScope.launch { checkQuotasAndSpeed(forceSync = false) }
            }
        }
        try {
            cm.registerDefaultNetworkCallback(networkCallback!!)
        } catch (e: Exception) {
            Log.e("VpnBlockService", "Failed to register network callback", e)
        }
    }

    private fun unregisterNetworkCallback() {
        networkCallback?.let { callback ->
            val cm = getSystemService(ConnectivityManager::class.java)
            try { 
                cm?.unregisterNetworkCallback(callback) 
            } catch (e: IllegalArgumentException) {
                Log.w("VpnBlockService", "Callback wasn't registered", e)
            } catch (e: Exception) {
                Log.e("VpnBlockService", "Unexpected error unregistering callback", e)
            }
        }
    }

    private fun startMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            var isFirstCheck = true
            while (isActive) {
                val shouldSyncSystem = isFirstCheck
                checkQuotasAndSpeed(forceSync = shouldSyncSystem)
                isFirstCheck = false
                delay(calculateAdaptiveDelay())
            }
        }
    }

    private fun calculateAdaptiveDelay(): Long {
        if (!isScreenOn) return QUOTA_SYNC_INTERVAL_MILLIS
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        
        return if (batteryLevel < 30) QUOTA_SYNC_INTERVAL_MILLIS else 1000L
    }

    private class GlobalTracker(
        var lastRtTotal: Long = 0L, 
        var accumulatedDelta: Long = 0L, 
        var lastDayStartMillis: Long = -1L,
        var lastBillingCycleStartMillis: Long = -1L,
        var lastSystemTotal: Long = 0L,
        var lastSystemWeek: Long = 0L,
        var lastSystemMonth: Long = 0L
    )
    private var wifiTracker = GlobalTracker()
    private var mobileTracker = GlobalTracker()
    
    private val appLastSystemUsage = mutableMapOf<String, Long>()
    private var lastGlobalBlock = false
    private var lastBlockedApps = emptySet<String>()
    private var requestedQuotaPackages = emptySet<String>()
    private var requestedGlobalBlock = false
    private var appliedQuotaPackages = emptySet<String>()
    private var appliedGlobalBlock = false
    private var appliedBlockedApps = emptySet<String>()
    private var monitoredQuotaCount = 0
    private var tunnelSinceTimestamp: Long? = null
    private var lastQuotaSyncTimestamp: Long? = null
    private var tunnelRebuildCount = 0
    private var vpnErrorCount = 0
    private var lastRecordedError: String? = null
    private var lastDiagnosedBlockedPackages = emptySet<String>()
    private var failedQuotaPackages = emptySet<String>()
    private var ruleErrorMessage: String? = null
    private var lastVpnState = false
    private var lastVpnAttemptAt = 0L
    private var lastWidgetUpdateTime = 0L
    private var lastQuotaSyncTime = 0L
    private val appUsageWifiCache = mutableMapOf<String, Long>()
    private val appUsageMobileCache = mutableMapOf<String, Long>()
    private var interceptorStateJob: Job? = null

    private suspend fun checkQuotasAndSpeed(forceSync: Boolean = false) = quotaMutex.withLock {
        val settings = try {
            repository.getSettings()
        } catch (e: Exception) {
            Log.e("ARVO_QUOTA", "Unable to read settings for quota enforcement", e)
            val message = getString(R.string.blocking_error_measurement)
            recordVpnError(message)
            RealTimeData.updateAppQuotaRuntime(
                RealTimeData.appQuotaRuntime.value.copy(
                    errorMessage = message
                )
            )
            updateVpnRuntimeSnapshot(
                blockRequired = requestedGlobalBlock || requestedQuotaPackages.isNotEmpty(),
                rebuilding = false,
                tunnelActive = vpnInterface != null,
                errorMessage = message
            )
            return@withLock
        }
        
        if (isScreenOn) {
            updateSpeedData()
        }

        val cm = getSystemService(ConnectivityManager::class.java) ?: return@withLock
        val activeTransports = getUnderlyingTransports(cm)
        val isActuallyWifi = activeTransports.first
        val isActuallyMobile = activeTransports.second
        
        // Suivi simultané et indépendant Wi-Fi et Mobile pour éviter qu'un réseau ne fige l'autre
        val currentTime = System.currentTimeMillis()
        val currentDayStart = getStartOfDayMillis(currentTime)
        val currentBillingCycleStart = usageManager.getBillingCycleStartMillis(settings.billingCycleDay)
        val isUserInArvo = lastPackageName == "io.arvo.dataconso"
        val shouldForceRefresh = forceSync ||
            wifiTracker.lastDayStartMillis != currentDayStart ||
            wifiTracker.lastBillingCycleStartMillis != currentBillingCycleStart ||
            mobileTracker.lastDayStartMillis != currentDayStart ||
            mobileTracker.lastBillingCycleStartMillis != currentBillingCycleStart ||
            (isUserInArvo && currentTime - lastWidgetUpdateTime > 5000) ||
            currentTime - lastQuotaSyncTime >= QUOTA_SYNC_INTERVAL_MILLIS

        // 1. Calcul Trafic Wi-Fi (Total - Mobile)
        val wifiRtDl = (TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()).coerceAtLeast(0L)
        val wifiRtUl = (TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()).coerceAtLeast(0L)
        val wifiRtTotal = wifiRtDl + wifiRtUl

        if (wifiTracker.lastRtTotal == 0L) wifiTracker.lastRtTotal = wifiRtTotal
        val wifiDelta = (wifiRtTotal - wifiTracker.lastRtTotal).coerceAtLeast(0L)
        wifiTracker.accumulatedDelta += wifiDelta
        wifiTracker.lastRtTotal = wifiRtTotal

        var wifiToday = wifiTracker.lastSystemTotal + wifiTracker.accumulatedDelta
        var wifiMonth = wifiTracker.lastSystemMonth + wifiTracker.accumulatedDelta

        // 2. Calcul Trafic Mobile
        val mobileRtDl = TrafficStats.getMobileRxBytes()
        val mobileRtUl = TrafficStats.getMobileTxBytes()
        val mobileRtTotal = mobileRtDl + mobileRtUl

        if (mobileTracker.lastRtTotal == 0L) mobileTracker.lastRtTotal = mobileRtTotal
        val mobileDelta = (mobileRtTotal - mobileTracker.lastRtTotal).coerceAtLeast(0L)
        mobileTracker.accumulatedDelta += mobileDelta
        mobileTracker.lastRtTotal = mobileRtTotal

        var mobileToday = mobileTracker.lastSystemTotal + mobileTracker.accumulatedDelta
        var mobileMonth = mobileTracker.lastSystemMonth + mobileTracker.accumulatedDelta

        if (shouldForceRefresh) {
            try {
                val activeSubscriptions = telephonyRepository.activeSubscriptions.value
                val defaultDataSub = activeSubscriptions.find { isDefault -> isDefault.isDefaultData }
                val subIdForCheck = defaultDataSub?.subscriptionId ?: -1

                val wifiUsageToday = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.DAILY, ConnectivityManager.TYPE_WIFI)
                val wifiUsageMonth = usageManager.getUsageBreakdownForBillingCycle(settings.billingCycleDay, ConnectivityManager.TYPE_WIFI)
                
                wifiTracker.lastSystemTotal = wifiUsageToday.totalBytes
                wifiTracker.lastSystemMonth = wifiUsageMonth.totalBytes
                wifiTracker.accumulatedDelta = 0L
                wifiTracker.lastDayStartMillis = currentDayStart
                wifiTracker.lastBillingCycleStartMillis = currentBillingCycleStart
                
                wifiToday = wifiTracker.lastSystemTotal
                wifiMonth = wifiTracker.lastSystemMonth

                val mobileUsageToday = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.DAILY, ConnectivityManager.TYPE_MOBILE, subId = subIdForCheck)
                val mobileUsageMonth = usageManager.getUsageBreakdownForBillingCycle(settings.billingCycleDay, ConnectivityManager.TYPE_MOBILE, subId = subIdForCheck)
                
                mobileTracker.lastSystemTotal = mobileUsageToday.totalBytes
                mobileTracker.lastSystemMonth = mobileUsageMonth.totalBytes
                mobileTracker.accumulatedDelta = 0L
                mobileTracker.lastDayStartMillis = currentDayStart
                mobileTracker.lastBillingCycleStartMillis = currentBillingCycleStart
                
                mobileToday = mobileTracker.lastSystemTotal
                mobileMonth = mobileTracker.lastSystemMonth

                // Mise à jour des quotas d'applications pour chaque transport
                var allQuotas = repository.getAllQuotas()
                val updatedQuotas = mutableListOf<AppQuotaEntity>()
                
                val cal = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
                }
                val startTime = cal.timeInMillis

                val packageNames = allQuotas.map { it.packageName }
                val wifiAppStats = usageManager.getMultiAppUsageStrict(
                    packageNames,
                    startTime,
                    currentTime,
                    ConnectivityManager.TYPE_WIFI
                )
                val mobileAppStats = usageManager.getMultiAppUsageStrict(
                    packageNames,
                    startTime,
                    currentTime,
                    ConnectivityManager.TYPE_MOBILE,
                    subIdForCheck
                )
                allQuotas.forEach { q ->
                    val appStats = maxOf(
                        AppQuotaPolicy.combineUsage(
                            q.networkType,
                            wifiAppStats[q.packageName]?.totalBytes ?: 0L,
                            mobileAppStats[q.packageName]?.totalBytes ?: 0L
                        ),
                        AppQuotaPolicy.combineUsage(
                            q.networkType,
                            RealTimeData.appUsagesWifi.value[q.packageName] ?: 0L,
                            RealTimeData.appUsagesMobile.value[q.packageName] ?: 0L
                        )
                    )

                    val isExceeded = AppQuotaPolicy.isQuotaExceeded(q, appStats)
                    Log.d(
                        "ARVO_QUOTA",
                        "quota package=${q.packageName} enabled=${q.isEnabled} " +
                            "network=${q.networkType} used=$appStats limit=${q.quotaBytes} exceeded=$isExceeded"
                    )
                    if (appStats != q.usedBytes || q.isBlocked != isExceeded) {
                        updatedQuotas.add(q.copy(usedBytes = appStats, isBlocked = isExceeded))
                    }
                }
                
                if (updatedQuotas.isNotEmpty()) {
                    repository.saveAllQuotas(updatedQuotas)
                    allQuotas = repository.getAllQuotas()
                }
                lastQuotaSyncTime = currentTime
                lastQuotaSyncTimestamp = currentTime
                lastRecordedError = null

                RealTimeData.updateAndBroadcast(
                    this@VpnBlockService,
                    wifiToday = wifiToday,
                    mobileToday = mobileToday,
                    wifiMonth = wifiMonth,
                    mobileMonth = mobileMonth
                )
            } catch (e: Exception) {
                Log.e("ARVO_QUOTA", "Usage sync failed; preserving the last known quota state", e)
                val message = getString(R.string.blocking_error_measurement)
                recordVpnError(message)
                RealTimeData.updateAppQuotaRuntime(
                    RealTimeData.appQuotaRuntime.value.copy(
                        measurementError = message
                    )
                )
                updateVpnRuntimeSnapshot(
                    blockRequired = requestedGlobalBlock || requestedQuotaPackages.isNotEmpty(),
                    rebuilding = false,
                    tunnelActive = vpnInterface != null,
                    errorMessage = message
                )
            }
        }

        val appsToBlock = mutableSetOf<String>()
        val quotaPackagesToBlock = mutableSetOf<String>()
        val allQuotas = try {
            repository.getAllQuotas()
        } catch (e: Exception) {
            Log.e("ARVO_QUOTA", "Unable to read app quota rules", e)
            val message = getString(R.string.blocking_error_measurement)
            recordVpnError(message)
            RealTimeData.updateAppQuotaRuntime(
                RealTimeData.appQuotaRuntime.value.copy(
                    errorMessage = message
                )
            )
            updateVpnRuntimeSnapshot(
                blockRequired = requestedGlobalBlock || requestedQuotaPackages.isNotEmpty(),
                rebuilding = false,
                tunnelActive = vpnInterface != null,
                errorMessage = message
            )
            return@withLock
        }
        monitoredQuotaCount = allQuotas.count { it.isEnabled }
        allQuotas.forEach { it ->
            if (it.packageName == HOST_PACKAGE) return@forEach

            // Correction : On applique le blocage si le quota est dépassé, sans exiger que isCorrectNetwork soit strictement égal au réseau actif instantané
            val isQuotaExceeded = AppQuotaPolicy.isQuotaExceeded(it, it.usedBytes)
            Log.d(
                "ARVO_QUOTA",
                "block decision package=${it.packageName} enabled=${it.isEnabled} " +
                    "used=${it.usedBytes} limit=${it.quotaBytes} exceeded=$isQuotaExceeded " +
                    "blocked=${it.isBlocked} manual=${it.isManualBlocked}"
            )
            // isBlocked is a cached/display field. The current measurement is
            // authoritative so raising a quota immediately releases the app.
            if (AppQuotaPolicy.shouldBlock(it, it.usedBytes)) {
                quotaPackagesToBlock.add(it.packageName)
                appsToBlock.add(it.packageName)
                if (it.packageName == "com.google.android.youtube") {
                    appsToBlock.add("com.google.android.youtube.tv")
                    appsToBlock.add("com.google.android.apps.youtube.music")
                    appsToBlock.add("com.google.android.apps.youtube.kids")
                    appsToBlock.add("com.google.android.apps.youtube.music.pwa")
                }
                if (it.packageName == "com.facebook.katana") appsToBlock.add("com.facebook.services")
            }
        }

        // A global quota belongs to the transport currently carrying traffic.
        // Do not let an exceeded mobile quota block an under-limit Wi-Fi link,
        // or vice versa. When no underlying transport is known, fail open.
        val activeBudget = when {
            isActuallyWifi -> settings.monthlyWifiGb
            isActuallyMobile -> settings.monthlyMobileGb
            else -> 0.0
        }
        val activeToday = when {
            isActuallyWifi -> wifiToday
            isActuallyMobile -> mobileToday
            else -> 0L
        }
        val activeMonth = when {
            isActuallyWifi -> wifiMonth
            isActuallyMobile -> mobileMonth
            else -> 0L
        }
        
        val todayGb = activeToday / 1073741824.0
        val monthUsageGb = activeMonth / 1073741824.0
        val projection = QuotaCalculator.calculateProjection(activeBudget, monthUsageGb, settings.billingCycleDay)
        val dailyLimit = if (settings.dailyLimitGb > 0) settings.dailyLimitGb else projection.recommendedDailyGb
        
        // Only the active transport can trigger the global block.
        val globalBlock = settings.vpnEnabled && ((dailyLimit > 0 && todayGb >= dailyLimit) || 
                          (activeBudget > 0 && monthUsageGb >= activeBudget))
        
        val hasEnabledQuotas = allQuotas.any { it.isEnabled }
        // The current native engine is a deny/black-hole engine, not a
        // forwarding/filtering proxy. Do not create a full-device tunnel for
        // passive firewall or Ghost Mode settings, otherwise all traffic is
        // blocked even when no quota is exceeded.
        val vpnProtectionRequired = globalBlock || appsToBlock.isNotEmpty()

        // Keep the foreground monitor alive while quotas exist, but only create
        // a VPN tunnel when there is something to block or filter.
        val shouldBeRunning = vpnProtectionRequired || hasEnabledQuotas || settings.vpnEnabled
        
        val rulesChanged = globalBlock != lastGlobalBlock || appsToBlock != lastBlockedApps
        val stateChanged = rulesChanged || shouldBeRunning != lastVpnState
        val appliedRulesMatch = globalBlock == appliedGlobalBlock &&
            (globalBlock || appsToBlock == appliedBlockedApps)
        val interceptorFailed = packetInterceptor.state.value.status == RealPacketInterceptor.Status.ERROR ||
            packetInterceptor.state.value.status == RealPacketInterceptor.Status.IDLE

        Log.d(
            "ARVO_BLOCK",
            "ARVO_BLOCK: evaluation globalBlock=$globalBlock appsToBlock=$appsToBlock " +
                "vpnEnabled=${settings.vpnEnabled} vpnRequired=$vpnProtectionRequired " +
                "daily=${todayGb} monthly=${monthUsageGb} dailyLimit=$dailyLimit activeBudget=$activeBudget"
        )
        
        if (stateChanged) {
            Log.d("ARVO_BLOCK", "ARVO_BLOCK: stateChanged -> globalBlock=$globalBlock, appsToBlock=$appsToBlock, shouldBeRunning=$shouldBeRunning")
            lastGlobalBlock = globalBlock; lastBlockedApps = appsToBlock.toSet(); lastVpnState = shouldBeRunning
        }

        recordBlockedPackageChanges(
            quotaPackagesToBlock,
            allQuotas.associateBy { it.packageName }
        )
        if (vpnProtectionRequired &&
            (vpnInterface == null || !appliedRulesMatch || interceptorFailed)
        ) {
            val now = System.currentTimeMillis()
            if (stateChanged || now - lastVpnAttemptAt >= 5000L) {
                lastVpnAttemptAt = now
                requestedQuotaPackages = quotaPackagesToBlock
                requestedGlobalBlock = globalBlock
                failedQuotaPackages = emptySet()
                ruleErrorMessage = null
                RealTimeData.updateAppQuotaRuntime(
                    AppQuotaRuntimeSnapshot(
                        globalBlocked = appliedGlobalBlock,
                        appliedPackages = appliedQuotaPackages,
                        pendingPackages = quotaPackagesToBlock - appliedQuotaPackages
                    )
                )
                updateVpnRuntimeSnapshot(
                    blockRequired = vpnProtectionRequired,
                    rebuilding = true,
                    tunnelActive = vpnInterface != null,
                    errorMessage = null
                )
                establishVpn(
                    globalBlock,
                    lastBlockedApps,
                    settings,
                    quotaPackagesToBlock
                )
            } else {
                Log.d("ARVO_VPN", "Protection remains enabled; waiting for tunnel retry")
            }
        }

        // A quota/settings refresh may occur without changing the monitoring
        // state. Always tear down the tunnel as soon as no protection is needed.
        if (!vpnProtectionRequired && vpnInterface != null) {
            Log.d("ARVO_BLOCK", "ARVO_BLOCK: no active rule, stopping VPN tunnel")
            stopVpn()
            ArvoWidgetProvider.triggerUpdate(this@VpnBlockService)
        }
        if (!vpnProtectionRequired && vpnInterface == null) {
            requestedQuotaPackages = emptySet()
            requestedGlobalBlock = false
            failedQuotaPackages = emptySet()
            ruleErrorMessage = null
            val currentRuntime = RealTimeData.appQuotaRuntime.value
            RealTimeData.updateAppQuotaRuntime(
                if (currentRuntime.measurementError != null) {
                    currentRuntime.copy(
                        globalBlocked = false,
                        appliedPackages = emptySet(),
                        pendingPackages = emptySet(),
                        failedPackages = emptySet(),
                        errorMessage = null
                    )
                } else {
                    AppQuotaRuntimeSnapshot()
                }
            )
        }

        updateVpnRuntimeSnapshot(
            blockRequired = vpnProtectionRequired,
            rebuilding = false,
            tunnelActive = vpnInterface != null &&
                packetInterceptor.state.value.status == RealPacketInterceptor.Status.ACTIVE,
            errorMessage = ruleErrorMessage
        )

        val now = System.currentTimeMillis()
        if (globalBlock || appsToBlock.isNotEmpty() || (now - lastWidgetUpdateTime > 5000)) {
            val statusNotif = if (globalBlock) getString(R.string.limit_exceeded)
            else if (appsToBlock.isNotEmpty()) resources.getQuantityString(R.plurals.notif_blocked_apps_count, appsToBlock.size, appsToBlock.size)
            else "${usageManager.formatData(activeToday)} / ${usageManager.formatData((dailyLimit * 1073741824).toLong())}"
            updateNotification("${getString(R.string.notif_prefix)} • ${usageManager.formatSpeed(lastDlSpeed)}", statusNotif)
            lastWidgetUpdateTime = now
        }
        
        if (settings.speedEnabled && isScreenOn) withContext(Dispatchers.Main) { showFloatingWindow() } else hideFloatingWindow()
    }

    private fun updateVpnRuntimeSnapshot(
        blockRequired: Boolean,
        rebuilding: Boolean,
        tunnelActive: Boolean,
        errorMessage: String? = ruleErrorMessage
    ) {
        val now = System.currentTimeMillis()
        if (tunnelActive && tunnelSinceTimestamp == null) tunnelSinceTimestamp = now
        if (!tunnelActive) tunnelSinceTimestamp = null
        RealTimeData.updateVpnRuntime(
            VpnRuntimeSnapshot(
                state = VpnDiagnosticsEngine.runtimeState(
                    blockRequired = blockRequired,
                    tunnelActive = tunnelActive,
                    rebuilding = rebuilding,
                    errorMessage = errorMessage
                ),
                tunnelActive = tunnelActive,
                monitoredApps = monitoredQuotaCount,
                blockedApps = appliedQuotaPackages.size,
                lastSyncTimestamp = lastQuotaSyncTimestamp,
                tunnelSinceTimestamp = tunnelSinceTimestamp,
                rebuildCount = tunnelRebuildCount,
                errorCount = vpnErrorCount,
                errorMessage = errorMessage
            )
        )
    }

    private fun recordVpnError(message: String) {
        if (message == lastRecordedError) return
        lastRecordedError = message
        vpnErrorCount++
        RealTimeData.recordVpnDiagnosticEvent(
            VpnDiagnosticEventType.ERROR,
            message
        )
    }

    private fun recordBlockedPackageChanges(
        requestedPackages: Set<String>,
        quotasByPackage: Map<String, AppQuotaEntity>
    ) {
        (requestedPackages - lastDiagnosedBlockedPackages).forEach { packageName ->
            val appName = quotasByPackage[packageName]?.appName ?: packageName
            RealTimeData.recordVpnDiagnosticEvent(
                VpnDiagnosticEventType.BLOCK_STARTED,
                "Demande de blocage pour $appName.",
                packageName
            )
        }
        (lastDiagnosedBlockedPackages - requestedPackages).forEach { packageName ->
            val appName = quotasByPackage[packageName]?.appName ?: packageName
            RealTimeData.recordVpnDiagnosticEvent(
                VpnDiagnosticEventType.BLOCK_RELEASED,
                "Fin du blocage pour $appName.",
                packageName
            )
        }
        lastDiagnosedBlockedPackages = requestedPackages
    }

    private fun getStartOfDayMillis(referenceTime: Long): Long {
        return java.util.Calendar.getInstance().apply {
            timeInMillis = referenceTime
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getUnderlyingTransports(cm: ConnectivityManager): Pair<Boolean, Boolean> {
        val active = cm.activeNetwork
        val activeCapabilities = active?.let { cm.getNetworkCapabilities(it) }
        if (activeCapabilities != null && !activeCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            val wifi = activeCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            val mobile = activeCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            if (wifi || mobile) {
                lastUnderlyingWifi = wifi
                lastUnderlyingMobile = mobile
                return wifi to mobile
            }
        }

        if (activeCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true &&
            (lastUnderlyingWifi || lastUnderlyingMobile)
        ) {
            return lastUnderlyingWifi to lastUnderlyingMobile
        }

        val networks = cm.allNetworks
        var wifi = false
        var mobile = false
        for (network in networks) {
            val capabilities = cm.getNetworkCapabilities(network) ?: continue
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) continue
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) wifi = true
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) mobile = true
        }
        if (wifi || mobile) {
            lastUnderlyingWifi = wifi
            lastUnderlyingMobile = mobile
        }
        return wifi to mobile
    }

    private fun establishVpn(
        global: Boolean,
        apps: Set<String>,
        settings: AppSettings,
        quotaPackages: Set<String>
    ) {
        try {
            val dns = try { DnsProvider.valueOf(settings.dnsProvider) } catch (_: Exception) { DnsProvider.ADGUARD }
            val builder = Builder()
                .setSession("ARVO 2.0")
                .setMtu(1500)
                .addAddress("10.0.0.1", 24)
                .addAddress("fd00:1:2:3::1", 128)
                .addDnsServer(dns.primary)
            
            if (dns.secondary.isNotEmpty()) builder.addDnsServer(dns.secondary)
            builder.addDnsServer("2606:4700:4700::1111")

            val isGhostMode = settings.ghostModeEnabled

            when {
                global -> {
                    builder.addRoute("0.0.0.0", 0)
                    builder.addRoute("::", 0)
                }
                apps.isNotEmpty() -> {
                    var allowedPackageCount = 0
                    apps.forEach { pkg -> 
                        if (pkg != HOST_PACKAGE) {
                            try {
                                builder.addAllowedApplication(pkg)
                                allowedPackageCount++
                            } catch (e: Exception) {
                                Log.w("ARVO_VPN", "Failed to add blocked application to VPN: $pkg", e)
                                if (pkg in quotaPackages) failedQuotaPackages += pkg
                            }
                        }
                    }
                    if (allowedPackageCount == 0) {
                        throw IllegalStateException("No blocked application could be assigned to the VPN")
                    }
                    builder.addDnsServer("10.0.0.1")
                    builder.addRoute("0.0.0.0", 0)
                    builder.addRoute("::", 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) builder.setBlocking(true)
                }
                isGhostMode -> {
                    builder.addRoute("0.0.0.0", 0)
                    builder.addRoute("::", 0)
                    packetInterceptor.updateBlockingState(global = false)
                }
            }
            
            val newInterface = builder.establish()
            if (newInterface != null) {
                val previousInterface = vpnInterface
                packetInterceptor.stopInterception()
                vpnInterface = newInterface
                tunnelSinceTimestamp = System.currentTimeMillis()
                tunnelRebuildCount++
                previousInterface?.close()
                appliedQuotaPackages = quotaPackages - failedQuotaPackages
                appliedGlobalBlock = global
                appliedBlockedApps = apps
                packetInterceptor.updateBlockingState(global)
                lastRecordedError = null
                RealTimeData.recordVpnDiagnosticEvent(
                    VpnDiagnosticEventType.TUNNEL_REBUILD,
                    "Tunnel VPN établi avec ${quotaPackages.size} règle(s) de quota."
                )
                appliedQuotaPackages.forEach { packageName ->
                    RealTimeData.recordVpnDiagnosticEvent(
                        VpnDiagnosticEventType.BLOCK_APPLIED,
                        "Règle de blocage appliquée au package $packageName.",
                        packageName
                    )
                }
                Log.d("ARVO_BLOCK", "ARVO_VPN: VPN established successfully, interface fd=${newInterface.fd}")
                packetInterceptor.startInterception(newInterface)
                Log.d("ARVO_BLOCK", "ARVO_NATIVE: native interception started")
                if (failedQuotaPackages.isNotEmpty()) {
                    val failed = failedQuotaPackages
                    ruleErrorMessage = getString(R.string.blocking_error_rule)
                    RealTimeData.updateAppQuotaRuntime(
                        AppQuotaRuntimeSnapshot(
                            globalBlocked = global,
                            appliedPackages = appliedQuotaPackages,
                            pendingPackages = emptySet(),
                            failedPackages = failed,
                            errorMessage = getString(R.string.blocking_error_rule)
                        )
                    )
                } else {
                    RealTimeData.updateAppQuotaRuntime(
                        AppQuotaRuntimeSnapshot(
                            globalBlocked = global,
                            appliedPackages = appliedQuotaPackages,
                            pendingPackages = quotaPackages - appliedQuotaPackages
                        )
                    )
                }
            } else {
                Log.e(
                    "ARVO_BLOCK",
                    "ARVO_VPN: builder.establish() returned null; user VPN consent is missing or another VPN owns the tunnel"
                )
                val message = getString(R.string.blocking_error_tunnel)
                ruleErrorMessage = message
                recordVpnError(message)
                failedQuotaPackages = quotaPackages - appliedQuotaPackages
                RealTimeData.updateAppQuotaRuntime(
                    AppQuotaRuntimeSnapshot(
                        globalBlocked = appliedGlobalBlock,
                        appliedPackages = appliedQuotaPackages,
                        failedPackages = failedQuotaPackages,
                        errorMessage = ruleErrorMessage
                    )
                )
            }
        } catch (e: Exception) { 
            Log.e("ARVO_BLOCK", "ARVO_VPN: VPN Failure", e)
            ruleErrorMessage = e.message ?: e.javaClass.simpleName
            recordVpnError(ruleErrorMessage ?: getString(R.string.blocking_error_unknown))
            failedQuotaPackages = quotaPackages - appliedQuotaPackages
            if (vpnInterface == null) {
                RealTimeData.updateAndBroadcast(this, isTunnelActive = false)
            }
            RealTimeData.updateAppQuotaRuntime(
                AppQuotaRuntimeSnapshot(
                    globalBlocked = appliedGlobalBlock,
                    appliedPackages = appliedQuotaPackages,
                    failedPackages = failedQuotaPackages,
                    errorMessage = ruleErrorMessage
                )
            )
        }
    }

    private fun updateSpeedData() {
        val now = TimeUtils.getMonotonicTime()
        val rx = TrafficStats.getTotalRxBytes()
        val tx = TrafficStats.getTotalTxBytes()
        if (lastTimestamp > 0) {
            val dt = (now - lastTimestamp) / 1000.0
            if (dt > 0) {
                lastDlSpeed = ((rx - lastRxBytes) / dt).toLong().coerceAtLeast(0L)
                lastUlSpeed = ((tx - lastTxBytes) / dt).toLong().coerceAtLeast(0L)
                RealTimeData.updateAndBroadcast(this, dl = lastDlSpeed, ul = lastUlSpeed)
            }
        }
        lastRxBytes = rx; lastTxBytes = tx; lastTimestamp = now
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showFloatingWindow() {
        if (floatingView != null) {
            speedTextView?.text = "⬇ ${usageManager.formatSpeed(lastDlSpeed)}  ⬆ ${usageManager.formatSpeed(lastUlSpeed)}"
            return
        }
        try {
            val wm = windowManager ?: return
            val layout = LayoutInflater.from(this).inflate(R.layout.floating_speed_bar, null)
            speedTextView = layout.findViewById(R.id.speed_text)
            layout.findViewById<View>(R.id.btn_close_floating).setOnClickListener {
                serviceScope.launch { repository.saveSettings(repository.getSettings().copy(speedEnabled = false)); hideFloatingWindow() }
            }
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
            val params = WindowManager.LayoutParams(160.dpToPx(), 32.dpToPx(), type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
            params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; params.y = 100
            var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f
            layout.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x; initialY = params.y
                        initialTouchX = event.rawX; initialTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> { 
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        try { wm.updateViewLayout(layout, params) } catch (_: Exception) {}
                        true
                    }
                    else -> false
                }
            }
            floatingView = layout; wm.addView(floatingView, params)
        } catch (e: Exception) { Log.e("VpnBlockService", "Float failed", e) }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
    private fun hideFloatingWindow() { 
        floatingView?.let { 
            try { windowManager?.removeView(it) } catch (e: Exception) { Log.e("VpnBlockService", "Hide failed", e) }
            floatingView = null; speedTextView = null 
        } 
    }
    private fun stopVpn() {
        vpnInterface?.close(); vpnInterface = null
        tunnelSinceTimestamp = null
        RealTimeData.updateAndBroadcast(this, isTunnelActive = false)
        packetInterceptor.stopInterception() 
        appliedQuotaPackages = emptySet()
        appliedGlobalBlock = false
        appliedBlockedApps = emptySet()
        RealTimeData.updateAppQuotaRuntime(AppQuotaRuntimeSnapshot())
        updateVpnRuntimeSnapshot(
            blockRequired = false,
            rebuilding = false,
            tunnelActive = false,
            errorMessage = null
        )
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = buildNotification(
            getString(R.string.notif_title),
            getString(R.string.notif_init)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notificationId,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(notificationId, notification)
        }
        serviceScope.launch { 
            try { 
                checkQuotasAndSpeed(forceSync = true) 
            } catch (e: Exception) { 
                Log.e("VpnBlockService", "Force sync on start error", e) 
            } 
        }
        startMonitoring()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "ARVO 2.0 Core", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, msg: String): Notification {
        val intent = Intent(this, MainActivity::class.java); val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, channelId).setContentTitle(title).setContentText(msg).setSmallIcon(R.drawable.logo).setContentIntent(pi).setOngoing(true).build()
    }

    private fun updateNotification(title: String, msg: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .notify(notificationId, buildNotification(title, msg))
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
