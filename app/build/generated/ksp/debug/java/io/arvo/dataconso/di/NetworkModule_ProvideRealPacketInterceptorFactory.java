package io.arvo.dataconso.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.network.RealPacketInterceptor;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NetworkModule_ProvideRealPacketInterceptorFactory implements Factory<RealPacketInterceptor> {
  @Override
  public RealPacketInterceptor get() {
    return provideRealPacketInterceptor();
  }

  public static NetworkModule_ProvideRealPacketInterceptorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RealPacketInterceptor provideRealPacketInterceptor() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideRealPacketInterceptor());
  }

  private static final class InstanceHolder {
    static final NetworkModule_ProvideRealPacketInterceptorFactory INSTANCE = new NetworkModule_ProvideRealPacketInterceptorFactory();
  }
}
