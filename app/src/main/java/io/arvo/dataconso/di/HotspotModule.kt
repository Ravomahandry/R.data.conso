package io.arvo.dataconso.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.arvo.dataconso.repository.DeviceHotspotTrafficCounters
import io.arvo.dataconso.repository.HotspotClock
import io.arvo.dataconso.repository.HotspotTrafficCounters
import io.arvo.dataconso.repository.SystemHotspotClock
import io.arvo.dataconso.repository.HotspotBatteryProvider
import io.arvo.dataconso.repository.HotspotQuotaProvider
import io.arvo.dataconso.repository.DeviceHotspotBatteryProvider
import io.arvo.dataconso.repository.SettingsHotspotQuotaProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HotspotModule {
    @Provides
    @Singleton
    fun provideHotspotTrafficCounters(): HotspotTrafficCounters =
        DeviceHotspotTrafficCounters()

    @Provides
    @Singleton
    fun provideHotspotClock(): HotspotClock = SystemHotspotClock()

    @Provides
    @Singleton
    fun provideHotspotQuotaProvider(provider: SettingsHotspotQuotaProvider): HotspotQuotaProvider =
        provider

    @Provides
    @Singleton
    fun provideHotspotBatteryProvider(provider: DeviceHotspotBatteryProvider): HotspotBatteryProvider =
        provider
}
