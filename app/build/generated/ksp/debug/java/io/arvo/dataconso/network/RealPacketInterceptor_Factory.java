package io.arvo.dataconso.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class RealPacketInterceptor_Factory implements Factory<RealPacketInterceptor> {
  @Override
  public RealPacketInterceptor get() {
    return newInstance();
  }

  public static RealPacketInterceptor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RealPacketInterceptor newInstance() {
    return new RealPacketInterceptor();
  }

  private static final class InstanceHolder {
    static final RealPacketInterceptor_Factory INSTANCE = new RealPacketInterceptor_Factory();
  }
}
