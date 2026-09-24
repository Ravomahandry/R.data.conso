package io.arvo.dataconso

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ArvoWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_WIDGET_REFRESH = "io.arvo.dataconso.WIDGET_REFRESH"
        
        fun triggerUpdate(context: Context) {
            val request = OneTimeWorkRequestBuilder<WidgetWorker>()
                .addTag("widget_update")
                .setInitialDelay(500, TimeUnit.MILLISECONDS)
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                "widget_refresh_${System.nanoTime()}",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        triggerUpdate(context)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        triggerUpdate(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action ?: return
        
        if (action == ACTION_WIDGET_REFRESH || action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            // Internal validation: action constant check is basic security, 
            // but triggerUpdate uses WorkManager which is more robust.
            triggerUpdate(context)
        }
    }
}
