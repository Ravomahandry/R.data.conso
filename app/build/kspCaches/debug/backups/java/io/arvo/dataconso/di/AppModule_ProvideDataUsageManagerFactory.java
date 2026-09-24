package io.arvo.dataconso.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.DataUsageManager;
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
public final class AppModule_ProvideDataUsageManagerFactory implements Factory<DataUsageManager> {
  private final Provider<Context> contextProvider;

  private AppModule_ProvideDataUsageManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DataUsageManager get() {
    return provideDataUsageManager(contextProvider.get());
  }

  public static AppModule_ProvideDataUsageManagerFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideDataUsageManagerFactory(contextProvider);
  }

  public static DataUsageManager provideDataUsageManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDataUsageManager(context));
  }
}
