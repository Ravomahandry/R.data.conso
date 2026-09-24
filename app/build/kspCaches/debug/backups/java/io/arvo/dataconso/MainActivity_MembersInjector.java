package io.arvo.dataconso;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import io.arvo.dataconso.backend.CloudSyncManager;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<CloudSyncManager> cloudSyncManagerProvider;

  private MainActivity_MembersInjector(Provider<CloudSyncManager> cloudSyncManagerProvider) {
    this.cloudSyncManagerProvider = cloudSyncManagerProvider;
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectCloudSyncManager(instance, cloudSyncManagerProvider.get());
  }

  public static MembersInjector<MainActivity> create(
      Provider<CloudSyncManager> cloudSyncManagerProvider) {
    return new MainActivity_MembersInjector(cloudSyncManagerProvider);
  }

  @InjectedFieldSignature("io.arvo.dataconso.MainActivity.cloudSyncManager")
  public static void injectCloudSyncManager(MainActivity instance,
      CloudSyncManager cloudSyncManager) {
    instance.cloudSyncManager = cloudSyncManager;
  }
}
