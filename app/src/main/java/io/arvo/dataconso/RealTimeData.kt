package io.arvo.dataconso

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize

/**
 * Hub de Données Temps Réel ARVO (Elite 2.0).
 * Optimisé pour la distinction stricte Wi-Fi vs Mobile par application.
 */
object RealTimeData {
    private const val ACTION_SYNC = "io.arvo.dataconso.SYNC_REALTIME"
    
    @Parcelize
    data class SyncData(
        val dl: Long = -1,
        val ul: Long = -1,
        val isRunning: Boolean? = null,
        val isTunnelActive: Boolean? = null,
        val wifiT: Long = -1,
        val mobileT: Long = -1,
        val wifiW: Long = -1,
        val mobileW: Long = -1,
        val wifiM: Long = -1,
        val mobileM: Long = -1,
        val appUsagesWifi: Map<String, Long>? = null,
        val appUsagesMobile: Map<String, Long>? = null
    ) : Parcelable

    private val _isVpnRunning = MutableStateFlow(false)
    val isVpnRunning = _isVpnRunning.asStateFlow()

    private val _isTunnelActive = MutableStateFlow(false)
    val isTunnelActive = _isTunnelActive.asStateFlow()

    private val _dlSpeed = MutableStateFlow(0L)
    val dlSpeed = _dlSpeed.asStateFlow()

    private val _ulSpeed = MutableStateFlow(0L)
    val ulSpeed = _ulSpeed.asStateFlow()

    private val _todayWifiBytes = MutableStateFlow(0L)
    val todayWifiBytes = _todayWifiBytes.asStateFlow()

    private val _todayMobileBytes = MutableStateFlow(0L)
    val todayMobileBytes = _todayMobileBytes.asStateFlow()

    private val _weekWifiBytes = MutableStateFlow(0L)
    val weekWifiBytes = _weekWifiBytes.asStateFlow()

    private val _weekMobileBytes = MutableStateFlow(0L)
    val weekMobileBytes = _weekMobileBytes.asStateFlow()

    private val _monthWifiBytes = MutableStateFlow(0L)
    val monthWifiBytes = _monthWifiBytes.asStateFlow()

    private val _monthMobileBytes = MutableStateFlow(0L)
    val monthMobileBytes = _monthMobileBytes.asStateFlow()

    private val _appUsagesWifi = MutableStateFlow<Map<String, Long>>(emptyMap())
    val appUsagesWifi = _appUsagesWifi.asStateFlow()

    private val _appUsagesMobile = MutableStateFlow<Map<String, Long>>(emptyMap())
    val appUsagesMobile = _appUsagesMobile.asStateFlow()

    fun updateAndBroadcast(
        context: Context, 
        dl: Long = -1, 
        ul: Long = -1, 
        isRunning: Boolean? = null, 
        isTunnelActive: Boolean? = null, 
        wifiToday: Long = -1, 
        mobileToday: Long = -1, 
        wifiWeek: Long = -1,
        mobileWeek: Long = -1,
        wifiMonth: Long = -1,
        mobileMonth: Long = -1,
        appUsagesWifi: Map<String, Long>? = null,
        appUsagesMobile: Map<String, Long>? = null
    ) {
        val data = SyncData(dl, ul, isRunning, isTunnelActive, wifiToday, mobileToday, wifiWeek, mobileWeek, wifiMonth, mobileMonth, appUsagesWifi, appUsagesMobile)
        val intent = Intent(ACTION_SYNC).apply {
            `package` = context.packageName
            putExtra("data", data)
        }
        applyData(data)
        context.sendBroadcast(intent)
    }

    fun updateMonthUsage(wifi: Long, mobile: Long) {
        _monthWifiBytes.value = wifi
        _monthMobileBytes.value = mobile
    }
    
    fun updateWeekUsage(wifi: Long, mobile: Long) {
        _weekWifiBytes.value = wifi
        _weekMobileBytes.value = mobile
    }

    private fun applyData(data: SyncData) {
        if (data.dl != -1L) _dlSpeed.value = data.dl
        if (data.ul != -1L) _ulSpeed.value = data.ul
        data.isRunning?.let { _isVpnRunning.value = it }
        data.isTunnelActive?.let { _isTunnelActive.value = it }
        if (data.wifiT != -1L) _todayWifiBytes.value = data.wifiT
        if (data.mobileT != -1L) _todayMobileBytes.value = data.mobileT
        if (data.wifiW != -1L) _weekWifiBytes.value = data.wifiW
        if (data.mobileW != -1L) _weekMobileBytes.value = data.mobileW
        if (data.wifiM != -1L) _monthWifiBytes.value = data.wifiM
        if (data.mobileM != -1L) _monthMobileBytes.value = data.mobileM
        data.appUsagesWifi?.let { _appUsagesWifi.value = it }
        data.appUsagesMobile?.let { _appUsagesMobile.value = it }
    }

    class SyncReceiver : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_SYNC) {
                val data = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("data", SyncData::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra("data")
                }
                data?.let { applyData(it) }
            }
        }
    }
}
