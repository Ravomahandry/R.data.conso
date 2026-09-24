package io.arvo.dataconso.network;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class DnsResolver_Factory implements Factory<DnsResolver> {
  private final Provider<Context> contextProvider;

  private DnsResolver_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DnsResolver get() {
    return newInstance(contextProvider.get());
  }

  public static DnsResolver_Factory create(Provider<Context> contextProvider) {
    return new DnsResolver_Factory(contextProvider);
  }

  public static DnsResolver newInstance(Context context) {
    return new DnsResolver(context);
  }
}
