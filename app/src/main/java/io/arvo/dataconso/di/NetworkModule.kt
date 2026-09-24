package io.arvo.dataconso.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.arvo.dataconso.network.DnsResolver
import io.arvo.dataconso.network.RealPacketInterceptor
import io.arvo.dataconso.data.DataRepository
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideDnsResolver(@ApplicationContext context: Context): DnsResolver {
        return DnsResolver(context)
    }

    @Provides
    @Singleton
    fun provideRealPacketInterceptor(): RealPacketInterceptor {
        return RealPacketInterceptor()
    }
}
