package io.arvo.dataconso.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.network.DnsResolver;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NetworkModule_ProvideDnsResolverFactory implements Factory<DnsResolver> {
  private final Provider<Context> contextProvider;

  private NetworkModule_ProvideDnsResolverFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DnsResolver get() {
    return provideDnsResolver(contextProvider.get());
  }

  public static NetworkModule_ProvideDnsResolverFactory create(Provider<Context> contextProvider) {
    return new NetworkModule_ProvideDnsResolverFactory(contextProvider);
  }

  public static DnsResolver provideDnsResolver(Context context) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideDnsResolver(context));
  }
}
