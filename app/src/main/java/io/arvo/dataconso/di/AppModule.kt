package io.arvo.dataconso.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.arvo.dataconso.AppListManager
import io.arvo.dataconso.ArvoAiEngine
import io.arvo.dataconso.DataUsageManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataUsageManager(@ApplicationContext context: Context): DataUsageManager {
        return DataUsageManager(context)
    }
}
