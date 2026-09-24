package io.arvo.dataconso;

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
public final class AppListManager_Factory implements Factory<AppListManager> {
  private final Provider<Context> contextProvider;

  private AppListManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppListManager get() {
    return newInstance(contextProvider.get());
  }

  public static AppListManager_Factory create(Provider<Context> contextProvider) {
    return new AppListManager_Factory(contextProvider);
  }

  public static AppListManager newInstance(Context context) {
    return new AppListManager(context);
  }
}
