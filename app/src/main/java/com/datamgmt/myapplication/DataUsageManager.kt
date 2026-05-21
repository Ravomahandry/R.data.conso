package com.datamgmt.myapplication

import android.Manifest
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import java.util.*

class DataUsageManager(
    private val context: Context
) {

    enum class PeriodType { DAILY, WEEKLY, MONTHLY, CUMULATIVE, CUSTOM_DATE }

    data class UsageBreakdown(
        val downloadBytes: Long = 0L,
        val uploadBytes: Long = 0L
    ) {
        val totalBytes: Long
            get() = downloadBytes + uploadBytes
    }

    fun getMobileUsageToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        return getUsageForRange(startTime, endTime)
    }

    fun getUsageForRange(startTime: Long, endTime: Long, subscriberId: String? = null): Long {
        return getUsageBreakdownForRange(startTime, endTime, subscriberId).totalBytes
    }

    fun getUsageBreakdownForRange(startTime: Long, endTime: Long, subscriberId: String? = null): UsageBreakdown {
        try {
            val networkStatsManager =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

            val bucket = networkStatsManager.querySummaryForDevice(
                ConnectivityManager.TYPE_MOBILE,
                subscriberId,
                startTime,
                endTime
            )

            return UsageBreakdown(
                downloadBytes = bucket.rxBytes,
                uploadBytes = bucket.txBytes
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return UsageBreakdown()
        }
    }

    fun getUsageForPeriod(period: PeriodType, subscriberId: String? = null): Long {
        return getUsageBreakdownForPeriod(period, subscriberId).totalBytes
    }

    fun getUsageBreakdownForPeriod(
        period: PeriodType,
        subscriberId: String? = null,
        selectedDateMillis: Long? = null
    ): UsageBreakdown {
        val calendar = Calendar.getInstance()
        val end = System.currentTimeMillis()

        when (period) {
            PeriodType.DAILY -> {
                setStartOfDay(calendar)
                return getUsageBreakdownForRange(calendar.timeInMillis, end, subscriberId)
            }
            PeriodType.WEEKLY -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                setStartOfDay(calendar)
                return getUsageBreakdownForRange(calendar.timeInMillis, end, subscriberId)
            }
            PeriodType.MONTHLY -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                setStartOfDay(calendar)
                return getUsageBreakdownForRange(calendar.timeInMillis, end, subscriberId)
            }
            PeriodType.CUMULATIVE -> {
                // Since install
                val installStart = 0L
                return getUsageBreakdownForRange(installStart, end, subscriberId)
            }
            PeriodType.CUSTOM_DATE -> {
                val selected = selectedDateMillis ?: end
                return getUsageBreakdownForDay(selected, subscriberId)
            }
        }
    }

    fun getUsageBreakdownForDay(dayMillis: Long, subscriberId: String? = null): UsageBreakdown {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dayMillis
        }
        setStartOfDay(calendar)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val end = minOf(calendar.timeInMillis, System.currentTimeMillis())
        return getUsageBreakdownForRange(start, end, subscriberId)
    }

    fun getActiveSubscriptions(): List<SubscriptionInfo> {
        return try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return emptyList()
            }
            val manager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            manager.activeSubscriptionInfoList ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        private fun setStartOfDay(calendar: Calendar) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        fun humanReadable(bytes: Long): String {
            val kb = 1024.0
            val mb = kb * 1024.0
            val gb = mb * 1024.0
            val d = bytes.toDouble()
            return when {
                d >= gb -> String.format(Locale.getDefault(), "%.2f Go", d / gb)
                d >= mb -> String.format(Locale.getDefault(), "%.2f Mo", d / mb)
                else -> String.format(Locale.getDefault(), "%.2f Ko", d / kb)
            }
        }
    }
}
