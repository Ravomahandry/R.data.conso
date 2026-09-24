package io.arvo.dataconso

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*

class DataUsageManager(private val context: Context) {

    companion object {
        const val BYTES_PER_KIB = 1024L
        const val BYTES_PER_MIB = BYTES_PER_KIB * 1024L
        const val BYTES_PER_GIB = BYTES_PER_MIB * 1024L

        fun humanReadable(bytes: Long): String {
            val safeBytes = bytes.coerceAtLeast(0L).toDouble()
            val kb = BYTES_PER_KIB.toDouble()
            val mb = BYTES_PER_MIB.toDouble()
            val gb = BYTES_PER_GIB.toDouble()
            return when {
                safeBytes >= gb -> String.format(Locale.US, "%.2f Go", safeBytes / gb)
                safeBytes >= mb -> String.format(Locale.US, "%.2f Mo", safeBytes / mb)
                safeBytes >= kb -> String.format(Locale.US, "%.2f Ko", safeBytes / kb)
                else -> String.format(Locale.US, "%d o", bytes.coerceAtLeast(0L))
            }
        }
    }

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpRaw(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    enum class PeriodType { DAILY, WEEKLY, MONTHLY }

    data class UsageBreakdown(val downloadBytes: Long, val uploadBytes: Long) {
        val totalBytes: Long get() = downloadBytes + uploadBytes
    }

    fun getUsageBreakdownForRange(startTime: Long, endTime: Long, networkType: Int, subId: Int = -1): UsageBreakdown {
        return try {
            val nsm = context.getSystemService(NetworkStatsManager::class.java) ?: return UsageBreakdown(0L, 0L)
            @Suppress("DEPRECATION")
            val queryType = if (networkType == NetworkCapabilities.TRANSPORT_WIFI) ConnectivityManager.TYPE_WIFI else ConnectivityManager.TYPE_MOBILE
            val subscriberId = getSubscriberIdForSub(subId)
            getUsageForType(nsm, queryType, subscriberId, startTime, endTime)
        } catch (e: Exception) { UsageBreakdown(0L, 0L) }
    }

    private fun getUsageForType(nsm: NetworkStatsManager, type: Int, subscriberId: String?, start: Long, end: Long): UsageBreakdown {
        return try {
            val deviceBucket = nsm.querySummaryForDevice(type, subscriberId, start, end)
            UsageBreakdown(deviceBucket.rxBytes, deviceBucket.txBytes)
        } catch (_: Exception) {
            try {
                var rx = 0L; var tx = 0L
                val stats = nsm.querySummary(type, subscriberId, start, end)
                val bucket = NetworkStats.Bucket()
                while (stats.hasNextBucket()) {
                    stats.getNextBucket(bucket)
                    rx += bucket.rxBytes; tx += bucket.txBytes
                }
                stats.close()
                UsageBreakdown(rx, tx)
            } catch (_: Exception) { UsageBreakdown(0L, 0L) }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getSubscriberIdForSub(subId: Int): String? {
        if (subId <= 0) return null
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.createForSubscriptionId(subId).subscriberId
        } catch (_: Exception) { null }
    }

    fun getAppUsageForRange(
        packageName: String,
        startTime: Long,
        endTime: Long,
        networkType: Int,
        subId: Int = -1
    ): UsageBreakdown {
        val nsm = context.getSystemService(NetworkStatsManager::class.java) ?: return UsageBreakdown(0L, 0L)
        return try {
            val pm = context.packageManager
            val uid = pm.getApplicationInfo(packageName, 0).uid
            @Suppress("DEPRECATION")
            val queryType = if (networkType == NetworkCapabilities.TRANSPORT_WIFI) ConnectivityManager.TYPE_WIFI else ConnectivityManager.TYPE_MOBILE
            val subscriberId = if (queryType == ConnectivityManager.TYPE_MOBILE) {
                getSubscriberIdForSub(subId)
            } else {
                null
            }
            val stats = nsm.queryDetailsForUid(queryType, subscriberId, startTime, endTime, uid)
            var rx = 0L; var tx = 0L
            val bucket = NetworkStats.Bucket()
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket); rx += bucket.rxBytes; tx += bucket.txBytes
            }
            stats.close()
            UsageBreakdown(rx, tx)
        } catch (e: Exception) { UsageBreakdown(0L, 0L) }
    }

    fun getAppUsageForDay(packageName: String, dayMillis: Long, networkType: Int): UsageBreakdown {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dayMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = calendar.timeInMillis - 1 // End of the day (23:59:59.999)

        return getAppUsageForRange(packageName, startTime, endTime, networkType)
    }

    fun getUidRealTimeUsage(packageName: String): Long {
        return try {
            val uid = context.packageManager.getApplicationInfo(packageName, 0).uid
            val rx = android.net.TrafficStats.getUidRxBytes(uid)
            val tx = android.net.TrafficStats.getUidTxBytes(uid)
            if (rx == android.net.TrafficStats.UNSUPPORTED.toLong()) 0L else rx + tx
        } catch (_: Exception) { 0L }
    }

    fun getUsageBreakdownForPeriod(period: PeriodType, networkType: Int, subId: Int = -1): UsageBreakdown {
        val cal = Calendar.getInstance(); val end = System.currentTimeMillis()
        when (period) {
            PeriodType.DAILY -> { cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0) }
            PeriodType.WEEKLY -> { cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0) }
            PeriodType.MONTHLY -> { cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0) }
        }
        return getUsageBreakdownForRange(cal.timeInMillis, end, networkType, subId)
    }

    fun getUsageBreakdownForBillingCycle(billingCycleDay: Int, networkType: Int, subId: Int = -1): UsageBreakdown {
        return getUsageBreakdownForRange(getBillingCycleStartMillis(billingCycleDay), System.currentTimeMillis(), networkType, subId)
    }

    fun getBillingCycleStartMillis(billingCycleDay: Int, now: Calendar = Calendar.getInstance()): Long {
        val normalized = billingCycleDay.coerceIn(1, 28)
        val cycleStart = now.clone() as Calendar
        cycleStart.set(Calendar.HOUR_OF_DAY, 0); cycleStart.set(Calendar.MINUTE, 0); cycleStart.set(Calendar.SECOND, 0); cycleStart.set(Calendar.MILLISECOND, 0)
        if (now.get(Calendar.DAY_OF_MONTH) < normalized) cycleStart.add(Calendar.MONTH, -1)
        cycleStart.set(Calendar.DAY_OF_MONTH, normalized.coerceAtMost(cycleStart.getActualMaximum(Calendar.DAY_OF_MONTH)))
        return cycleStart.timeInMillis
    }

    fun formatData(bytes: Long): String {
        val b = bytes.toDouble().coerceAtLeast(0.0)
        val kb = BYTES_PER_KIB.toDouble()
        val mb = BYTES_PER_MIB.toDouble()
        val gb = BYTES_PER_GIB.toDouble()
        return when {
            b >= gb -> context.getString(R.string.unit_gb, b / gb)
            b >= mb -> context.getString(R.string.unit_mb, b / mb)
            b >= kb -> context.getString(R.string.unit_kb, b / kb)
            else -> context.getString(R.string.unit_bytes, bytes)
        }
    }

    fun formatDataCompact(bytes: Long): String {
        val b = bytes.toDouble().coerceAtLeast(0.0); val kb = 1024.0; val mb = kb * 1024.0; val gb = mb * 1024.0
        return when {
            b >= gb -> String.format(Locale.US, "%.1fG", b / gb)
            b >= mb -> String.format(Locale.US, "%.0fM", b / mb)
            else -> String.format(Locale.US, "%.0fK", b / kb)
        }
    }

    fun formatSpeed(bytesPerSec: Long): String {
        val b = bytesPerSec.toDouble().coerceAtLeast(0.0); val kb = 1024.0; val mb = kb * 1024.0; val gb = mb * 1024.0
        return when {
            b >= mb -> context.getString(R.string.unit_speed_mbs, b / mb)
            b >= kb -> context.getString(R.string.unit_speed_kbs, b / kb)
            else -> context.getString(R.string.unit_speed_bytes, bytesPerSec)
        }
    }

    private val labelCache = mutableMapOf<String, String>()

    fun getTopAppsUsage(startTime: Long, endTime: Long, networkType: Int, subId: Int = -1): List<AppUsageInfo> {
        val nsm = context.getSystemService(NetworkStatsManager::class.java) ?: return emptyList()
        val pm = context.packageManager; val statsMap = mutableMapOf<Int, Long>()
        val subscriberId = getSubscriberIdForSub(subId)
        try {
            val stats = nsm.querySummary(networkType, subscriberId, startTime, endTime)
            val bucket = NetworkStats.Bucket()
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket); statsMap[bucket.uid] = (statsMap[bucket.uid] ?: 0L) + bucket.rxBytes + bucket.txBytes
            }
            stats.close()
        } catch (e: Exception) { }
        
        // Cache local des UIDs pour cette requête pour éviter getPackagesForUid répété
        val uidToPkg = mutableMapOf<Int, String>()

        return statsMap.asSequence()
            .filter { it.value > 10_000 } // Seuil abaissé à 10 Ko pour plus de réactivité en test
            .map { (uid, bytes) ->
                val pkg = uidToPkg.getOrPut(uid) {
                    val pkgs = pm.getPackagesForUid(uid)
                    if (!pkgs.isNullOrEmpty()) pkgs[0] else "UID: $uid"
                }
                val name = labelCache.getOrPut(pkg) {
                    if (pkg.startsWith("UID:")) "App $uid"
                    else {
                        try {
                            pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
                        } catch (e: Exception) {
                            pkg.split('.').last()
                        }
                    }
                }
                AppUsageInfo(pkg, name, bytes)
            }
            .sortedByDescending { it.bytes }
            .take(5)
            .toList()
    }

    fun getMultiAppUsage(
        packageNames: List<String>,
        startTime: Long,
        endTime: Long,
        networkType: Int,
        subId: Int = -1
    ): Map<String, UsageBreakdown> {
        val nsm = context.getSystemService(NetworkStatsManager::class.java) ?: return emptyMap()
        val pm = context.packageManager; val resultMap = mutableMapOf<String, UsageBreakdown>()
        val pkgToUid = packageNames.associateWith { try { pm.getApplicationInfo(it, 0).uid } catch (_: Exception) { -1 } }.filterValues { it != -1 }
        if (pkgToUid.isEmpty()) return emptyMap()
        try {
            val subscriberId = if (networkType == ConnectivityManager.TYPE_MOBILE) {
                getSubscriberIdForSub(subId)
            } else {
                null
            }
            val stats = nsm.querySummary(networkType, subscriberId, startTime, endTime)
            val bucket = NetworkStats.Bucket(); val uidStats = mutableMapOf<Int, UsageBreakdown>()
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                val current = uidStats[bucket.uid] ?: UsageBreakdown(0L, 0L)
                uidStats[bucket.uid] = UsageBreakdown(current.downloadBytes + bucket.rxBytes, current.uploadBytes + bucket.txBytes)
            }
            stats.close()
            pkgToUid.forEach { (pkg, uid) -> resultMap[pkg] = uidStats[uid] ?: UsageBreakdown(0L, 0L) }
        } catch (e: Exception) { }
        return resultMap
    }
}
