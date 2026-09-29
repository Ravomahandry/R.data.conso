package io.arvo.dataconso.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.arvo.dataconso.domain.HotspotStateDetector
import io.arvo.dataconso.domain.NetworkInterfaceHotspotStateDetector
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HotspotDetectorModule {
    @Provides
    @Singleton
    fun provideHotspotStateDetector(): HotspotStateDetector =
        NetworkInterfaceHotspotStateDetector()
}
