package io.arvo.dataconso

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: DataRepository,
    private val telephonyRepository: TelephonyRepository,
    private val aiEngine: ArvoAiEngine
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val context = applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, ArvoWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(component)

        if (appWidgetIds.isEmpty()) return@withContext Result.success()

        try {
            val settings = repository.getSettings()
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val caps = cm.getNetworkCapabilities(cm.activeNetwork)

            val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
            val source = if (isWifi) NetworkSource.WIFI else NetworkSource.MOBILE

            telephonyRepository.refreshSimInfo()

            val budget = if (isWifi) settings.monthlyWifiGb else settings.monthlyMobileGb
            val usageMonth = repository.getUsageForBillingCycle(source, settings.billingCycleDay)
            val usageToday = repository.getUsage(source, DataUsageManager.PeriodType.DAILY, settings)

            val usedGb = usageMonth.totalBytes / 1073741824.0
            val remainingGb = (budget - usedGb).coerceAtLeast(0.0)

            val daysLeft = QuotaCalculator.calculateDaysRemaining(settings.billingCycleDay)
            val dailyLimitGb = if (settings.dailyLimitGb > 0) settings.dailyLimitGb else (remainingGb / daysLeft.coerceAtLeast(1))
            
            val insights = aiEngine.generateInsights(usageMonth.totalBytes)
            val topInsight = insights.firstOrNull()

            val formatter = DataUsageManager(context)

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.arvo_widget)

                // 1. Source Label
                val sourceLabel = if (isWifi) context.getString(R.string.source_wifi) else context.getString(R.string.source_mobile)
                views.setTextViewText(R.id.widget_sim_label, "$sourceLabel ${context.getString(R.string.arvo_active)}")
                
                // ✅ A11y: Source
                val sourceA11y = context.getString(R.string.widget_a11y_source, sourceLabel)
                views.setCharSequence(R.id.widget_sim_label, "setContentDescription", sourceA11y)

                // 2. Data Remaining
                val remainingText = formatter.formatData((remainingGb * 1073741824).toLong())
                views.setTextViewText(R.id.widget_data_remaining, remainingText)
                
                // ✅ A11y: Data Remaining
                val remainingA11y = context.getString(R.string.widget_a11y_remaining, remainingText)
                views.setCharSequence(R.id.widget_data_remaining, "setContentDescription", remainingA11y)

                // 3. Today Usage
                val todayText = formatter.formatData(usageToday.totalBytes)
                views.setTextViewText(R.id.widget_today_usage, todayText)
                
                // ✅ A11y: Today Usage
                val todayA11y = context.getString(R.string.widget_a11y_today, todayText)
                views.setCharSequence(R.id.widget_today_usage, "setContentDescription", todayA11y)

                // 4. Daily Advice
                val advice = if (topInsight != null && topInsight.priority > 8) {
                    topInsight.title 
                } else {
                    val formattedLimit = formatter.formatData((dailyLimitGb * 1073741824).toLong())
                    context.getString(R.string.widget_daily_max, formattedLimit)
                }
                views.setTextViewText(R.id.widget_daily_advice, advice)

                // 5. Progress Gauge
                val progress = if (dailyLimitGb > 0) {
                    ((usageToday.totalBytes / 1073741824.0) / dailyLimitGb * 100).toInt().coerceIn(0, 100)
                } else {
                    0
                }
                views.setProgressBar(R.id.widget_progress, 100, progress, false)
                
                // ✅ A11y: Progress
                val usedGbStr = String.format(Locale.getDefault(), "%.1f", usageToday.totalBytes / 1073741824.0)
                val limitGbStr = String.format(Locale.getDefault(), "%.1f", dailyLimitGb)
                val progressA11y = context.getString(R.string.widget_a11y_progress, progress, usedGbStr, limitGbStr)
                views.setCharSequence(R.id.widget_progress, "setContentDescription", progressA11y)

                // Status Color
                val accentColor = if (progress > 90) 0xFFEF4444.toInt() else 0xFF38BDF8.toInt()
                views.setInt(R.id.widget_source_icon, "setColorFilter", accentColor)
                views.setTextColor(R.id.widget_sim_label, accentColor)

                // Pending Intents
                val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                val refreshIntent = Intent(context, ArvoWidgetProvider::class.java).apply { 
                    action = ArvoWidgetProvider.ACTION_WIDGET_REFRESH 
                }
                views.setOnClickPendingIntent(R.id.widget_btn_refresh, PendingIntent.getBroadcast(context, 20, refreshIntent, flag))

                val mainIntent = Intent(context, MainActivity::class.java)
                views.setOnClickPendingIntent(R.id.widget_container, PendingIntent.getActivity(context, 21, mainIntent, flag))

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("WidgetWorker", "Failed to update widget", e)
            Result.retry()
        }
    }
}

