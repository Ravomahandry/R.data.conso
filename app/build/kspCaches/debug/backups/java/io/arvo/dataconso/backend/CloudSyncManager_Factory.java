package io.arvo.dataconso.backend;

import com.google.firebase.analytics.FirebaseAnalytics;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.data.DataRepository;
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
public final class CloudSyncManager_Factory implements Factory<CloudSyncManager> {
  private final Provider<DataRepository> repositoryProvider;

  private final Provider<FirebaseAnalytics> analyticsProvider;

  private CloudSyncManager_Factory(Provider<DataRepository> repositoryProvider,
      Provider<FirebaseAnalytics> analyticsProvider) {
    this.repositoryProvider = repositoryProvider;
    this.analyticsProvider = analyticsProvider;
  }

  @Override
  public CloudSyncManager get() {
    return newInstance(repositoryProvider.get(), analyticsProvider.get());
  }

  public static CloudSyncManager_Factory create(Provider<DataRepository> repositoryProvider,
      Provider<FirebaseAnalytics> analyticsProvider) {
    return new CloudSyncManager_Factory(repositoryProvider, analyticsProvider);
  }

  public static CloudSyncManager newInstance(DataRepository repository,
      FirebaseAnalytics analytics) {
    return new CloudSyncManager(repository, analytics);
  }
}
