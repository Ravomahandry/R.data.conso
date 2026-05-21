package com.datamgmt.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UsageWorker2(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val usageManager = DataUsageManager(context)
        val database = AppDatabase.getDatabase(context)
        val settingsDao = database.settingsDao()
        val historyDao = database.historyDao()

        val settings = settingsDao.getSettings() ?: AppSettings()

        val calendar = Calendar.getInstance()
        val remainingDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + 1

        // Global usage
        val todayBytes = usageManager.getMobileUsageToday()
        val todayGb = todayBytes / (1024.0 * 1024.0 * 1024.0)
        val monthBytes = usageManager.getMobileUsageThisMonth()
        val monthGb = monthBytes / (1024.0 * 1024.0 * 1024.0)
        val usedBeforeToday = (monthGb - todayGb).coerceAtLeast(0.0)
        val dailyQuotaGb = QuotaCalculator.calculateDailyQuota(
            settings.monthlyQuotaGb,
            usedBeforeToday,
            remainingDays
        )

        // Per-SIM quota checking
        var anySimOverQuota = false
        val subscriberIds = getSubscriberIds(context)
        if (subscriberIds.isNotEmpty()) {
            for ((index, subId) in subscriberIds.withIndex()) {
                val simQuota = if (index == 0) settings.sim1QuotaGb else settings.sim2QuotaGb
                val simTodayBreakdown = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.DAILY, subId)
                val simMonthBreakdown = usageManager.getUsageBreakdownForPeriod(DataUsageManager.PeriodType.MONTHLY, subId)
                val simTodayGb = simTodayBreakdown.totalBytes / (1024.0 * 1024.0 * 1024.0)
                val simMonthGb = simMonthBreakdown.totalBytes / (1024.0 * 1024.0 * 1024.0)
                val simUsedBefore = (simMonthGb - simTodayGb).coerceAtLeast(0.0)
                val simDailyQuota = QuotaCalculator.calculateDailyQuota(simQuota, simUsedBefore, remainingDays)
                if (simDailyQuota > 0 && simTodayGb >= simDailyQuota) {
                    anySimOverQuota = true
                }

                // Save per-SIM history
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateLabel = sdf.format(System.currentTimeMillis())
                historyDao.insert(HistoryEntry(
                    timestamp = System.currentTimeMillis(),
                    dateLabel = dateLabel,
                    simId = "SIM${index + 1}",
                    bytes = simTodayBreakdown.totalBytes
                ))
            }
        }

        // VPN blocking: activate if global or any SIM quota exceeded
        val intent = Intent(context, VpnBlockService::class.java)
        val globalOverQuota = dailyQuotaGb > 0 && todayGb >= dailyQuotaGb
        if (globalOverQuota || anySimOverQuota) {
            context.startService(intent)
        } else {
            context.stopService(intent)
        }

        // Save settings
        settingsDao.saveSettings(settings.copy(
            currentUsageBytes = monthBytes,
            lastCheckTime = System.currentTimeMillis()
        ))

        // Save global history
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateLabel = sdf.format(System.currentTimeMillis())
        historyDao.insert(HistoryEntry(
            timestamp = System.currentTimeMillis(),
            dateLabel = dateLabel,
            simId = "TOTAL",
            bytes = todayBytes
        ))

        // Threshold notifications (global)
        val quotaBytes = (settings.monthlyQuotaGb * 1024.0 * 1024.0 * 1024.0).toLong()
        if (quotaBytes > 0L) {
            val percent = (monthBytes * 100) / quotaBytes
            val notifier = NotificationHelper(context)
            if (percent >= 100) notifier.notifyThreshold(100, settings.monthlyQuotaGb, monthBytes)
            else if (percent >= 80) notifier.notifyThreshold(80, settings.monthlyQuotaGb, monthBytes)
            else if (percent >= 50) notifier.notifyThreshold(50, settings.monthlyQuotaGb, monthBytes)
        }

        return Result.success()
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getSubscriberIds(context: Context): List<String> {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }
        return try {
            val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subs = subManager.activeSubscriptionInfoList ?: return emptyList()
            subs.take(2).map { sub ->
                try {
                    val tm = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                        .createForSubscriptionId(sub.subscriptionId)
                    tm.subscriberId?.takeIf { it.isNotBlank() }
                        ?: sub.iccId
                        ?: sub.subscriptionId.toString()
                } catch (e: Exception) {
                    sub.iccId ?: sub.subscriptionId.toString()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
