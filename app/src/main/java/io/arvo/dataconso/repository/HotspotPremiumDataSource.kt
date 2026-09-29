package io.arvo.dataconso.repository

import android.content.Context
import android.os.BatteryManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.arvo.dataconso.DataRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface HotspotQuotaProvider {
    fun monthlyQuotaBytes(): Flow<Long>
}

interface HotspotBatteryProvider {
    fun batteryPercent(): Int?
}

class SettingsHotspotQuotaProvider @Inject constructor(
    private val dataRepository: DataRepository
) : HotspotQuotaProvider {
    override fun monthlyQuotaBytes(): Flow<Long> =
        dataRepository.settingsFlow.map { settings ->
            (settings.monthlyMobileGb * 1_073_741_824.0)
                .coerceIn(0.0, Long.MAX_VALUE.toDouble())
                .toLong()
        }
}

class DeviceHotspotBatteryProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : HotspotBatteryProvider {
    override fun batteryPercent(): Int? =
        context.getSystemService(BatteryManager::class.java)
            ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            ?.takeIf { it in 0..100 }
}
