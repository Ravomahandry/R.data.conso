package io.arvo.dataconso

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppListManager @Inject constructor(@ApplicationContext private val context: Context) {
    fun getInstalledApps(): List<AppUsageInfo> {
        return try {
            val pm = context.packageManager
            // Use 0 flags for minimal memory impact
            val apps = pm.getInstalledApplications(0)
            apps.filter { appInfo ->
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || 
                (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            }.map { appInfo ->
                AppUsageInfo(
                    packageName = appInfo.packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    bytes = 0L
                )
            }.sortedBy { it.appName }
        } catch (e: Exception) {
            Log.e("AppList", "Error getting apps", e)
            emptyList()
        }
    }
}
