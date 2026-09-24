package io.arvo.dataconso.ui.onboarding

import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    data class PermissionState(
        val hasUsageStats: Boolean = false,
        val hasOverlay: Boolean = false,
        val isIgnoringBattery: Boolean = false,
        val isAccessibilityEnabled: Boolean = false,
        val isVpnConfigured: Boolean = false,
        val currentStep: Int = 1
    )

    private val _uiState = MutableStateFlow(PermissionState())
    val uiState = _uiState.asStateFlow()

    fun checkPermissions() {
        val usage = checkUsageStats()
        val overlay = Settings.canDrawOverlays(getApplication())
        val power = isIgnoringBatteryOptimizations()
        val accessibility = isAccessibilityServiceEnabled(getApplication(), io.arvo.dataconso.ArvoAccessibilityService::class.java)
        
        _uiState.value = _uiState.value.copy(
            hasUsageStats = usage,
            hasOverlay = overlay,
            isIgnoringBattery = power,
            isAccessibilityEnabled = accessibility,
            currentStep = when {
                !usage -> 1
                !overlay -> 2
                !accessibility -> 3
                !power -> 4
                else -> 5
            }
        )
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
        val expectedComponentName = android.content.ComponentName(context, service)
        val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        val colonSplitter = android.text.TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = android.content.ComponentName.unflattenFromString(componentNameString)
            if (enabledService != null && enabledService == expectedComponentName) return true
        }
        return false
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = getApplication<Application>().getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        return pm.isIgnoringBatteryOptimizations(getApplication<Application>().packageName)
    }

    private fun checkUsageStats(): Boolean {
        val appOps = getApplication<Application>().getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = @Suppress("DEPRECATION") appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            getApplication<Application>().packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }
}
