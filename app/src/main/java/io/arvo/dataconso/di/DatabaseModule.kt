package io.arvo.dataconso.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.arvo.dataconso.data.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // Hilt garantit que cet appel est fait une seule fois (Singleton)
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase) = database.settingsDao()

    @Provides
    @Singleton
    fun provideHistoryDao(database: AppDatabase) = database.historyDao()

    @Provides
    @Singleton
    fun provideQuotaDao(database: AppDatabase) = database.quotaDao()
}
