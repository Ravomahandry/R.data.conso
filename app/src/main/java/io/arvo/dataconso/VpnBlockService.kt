package io.arvo.dataconso

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import dagger.hilt.android.AndroidEntryPoint
import io.arvo.dataconso.network.DnsResolver
import io.arvo.dataconso.network.RealPacketInterceptor
import io.arvo.dataconso.util.TimeUtils
import kotlinx.coroutines.*
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
    }

    override fun onCreate() {
        super.onCreate()
        RealTimeData.updateAndBroadcast(this, isRunning = true)
        serviceScope.launch { dnsResolver.loadBlockList() }
        val filter = IntentFilter().apply { 
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction("io.arvo.dataconso.APP_CHANGED")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(screenReceiver, filter)
        }
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        registerNetworkCallback()
    }

    override fun onDestroy() {
        RealTimeData.updateAndBroadcast(this, isRunning = false)
        monitoringJob?.cancel()
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
                val shouldSyncSystem = isFirstCheck || !isScreenOn
                checkQuotasAndSpeed(forceSync = shouldSyncSystem)
                isFirstCheck = false
                delay(calculateAdaptiveDelay())
            }
        }
    }

    private fun calculateAdaptiveDelay(): Long {
        if (!isScreenOn) return 30000L
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        
        return when {
            batteryLevel < 15 -> 10000L
            batteryLevel < 30 -> 5000L
            else -> 1000L
        }
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
    private var lastVpnState = false
    private var lastVpnAttemptAt = 0L
    private var lastWidgetUpdateTime = 0L
    private var lastQuotaSyncTime = 0L
    private val appUsageWifiCache = mutableMapOf<String, Long>()
    private val appUsageMobileCache = mutableMapOf<String, Long>()

    private suspend fun checkQuotasAndSpeed(forceSync: Boolean = false) = quotaMutex.withLock {
        val settings = try { repository.getSettings() } catch (_: Throwable) { return@withLock }
        
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
            currentTime - lastQuotaSyncTime > 5000

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

                allQuotas.forEach { q ->
                    val qTransport = when (q.networkType) {
                        "WIFI" -> ConnectivityManager.TYPE_WIFI
                        "MOBILE" -> ConnectivityManager.TYPE_MOBILE
                        else -> ConnectivityManager.TYPE_WIFI // pour BOTH ou défaut, on évalue le Wi-Fi ou mobile selon dispo
                    }
                    val appStats = if (q.networkType == "BOTH") {
                        val w = usageManager.getAppUsageForRange(q.packageName, startTime, currentTime, ConnectivityManager.TYPE_WIFI).totalBytes
                        val m = usageManager.getAppUsageForRange(q.packageName, startTime, currentTime, ConnectivityManager.TYPE_MOBILE, subIdForCheck).totalBytes
                        w + m
                    } else {
                        usageManager.getAppUsageForRange(
                            q.packageName,
                            startTime,
                            currentTime,
                            qTransport,
                            if (qTransport == ConnectivityManager.TYPE_MOBILE) subIdForCheck else -1
                        ).totalBytes
                    }
                    
                    val isExceeded = q.quotaBytes > 0 && appStats >= q.quotaBytes
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

                RealTimeData.updateAndBroadcast(
                    this@VpnBlockService,
                    wifiToday = wifiToday,
                    mobileToday = mobileToday,
                    wifiMonth = wifiMonth,
                    mobileMonth = mobileMonth
                )
            } catch (e: Exception) { Log.e("ARVO_QUOTA", "Sync failed", e) }
        }

        val appsToBlock = mutableSetOf<String>()
        var allQuotas = repository.getAllQuotas()
        allQuotas.forEach { it ->
            if (it.packageName == HOST_PACKAGE) return@forEach

            // Correction : On applique le blocage si le quota est dépassé, sans exiger que isCorrectNetwork soit strictement égal au réseau actif instantané
            val isQuotaExceeded = it.quotaBytes > 0 && it.usedBytes >= it.quotaBytes
            Log.d(
                "ARVO_QUOTA",
                "block decision package=${it.packageName} enabled=${it.isEnabled} " +
                    "used=${it.usedBytes} limit=${it.quotaBytes} exceeded=$isQuotaExceeded " +
                    "blocked=${it.isBlocked} manual=${it.isManualBlocked}"
            )
            // isBlocked is a cached/display field. The current measurement is
            // authoritative so raising a quota immediately releases the app.
            if (it.isEnabled && (isQuotaExceeded || it.isManualBlocked)) {
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
        
        val stateChanged = globalBlock != lastGlobalBlock || appsToBlock != lastBlockedApps || shouldBeRunning != lastVpnState

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

        if (vpnProtectionRequired && vpnInterface == null) {
            val now = System.currentTimeMillis()
            if (stateChanged || now - lastVpnAttemptAt >= 5000L) {
                lastVpnAttemptAt = now
                establishVpn(globalBlock, lastBlockedApps, settings)
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

    private fun establishVpn(global: Boolean, apps: Set<String>, settings: AppSettings) {
        stopVpn()
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
                    packetInterceptor.updateBlockingState(global = true)
                }
                apps.isNotEmpty() -> {
                    apps.forEach { pkg -> 
                        if (pkg != HOST_PACKAGE) {
                            try { builder.addAllowedApplication(pkg) } catch (e: Exception) { Log.w("ARVO_VPN", "Failed to add allowed app: $pkg", e) }
                        }
                    }
                    if (apps.any { it.contains("google") }) try { builder.addAllowedApplication("com.google.android.gms") } catch (_: Exception) {}
                    builder.addDnsServer("10.0.0.1")
                    builder.addRoute("0.0.0.0", 0)
                    builder.addRoute("::", 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) builder.setBlocking(true)
                    packetInterceptor.updateBlockingState(global = false)
                }
                isGhostMode -> {
                    builder.addRoute("0.0.0.0", 0)
                    builder.addRoute("::", 0)
                    packetInterceptor.updateBlockingState(global = false)
                }
            }
            
            vpnInterface = builder.establish()
            if (vpnInterface != null) {
                Log.d("ARVO_BLOCK", "ARVO_VPN: VPN established successfully, interface fd=${vpnInterface?.fd}")
                packetInterceptor.startInterception(vpnInterface!!)
                Log.d("ARVO_BLOCK", "ARVO_NATIVE: native interception started")
                RealTimeData.updateAndBroadcast(this, isTunnelActive = true)
            } else {
                Log.e(
                    "ARVO_BLOCK",
                    "ARVO_VPN: builder.establish() returned null; user VPN consent is missing or another VPN owns the tunnel"
                )
            }
        } catch (e: Exception) { 
            Log.e("ARVO_BLOCK", "ARVO_VPN: VPN Failure", e) 
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
        RealTimeData.updateAndBroadcast(this, isTunnelActive = false)
        packetInterceptor.stopInterception() 
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
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(notificationId, buildNotification(title, msg))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
