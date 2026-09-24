package io.arvo.dataconso.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.DataUsageManager;
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
public final class DataRepository_Factory implements Factory<DataRepository> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<DataUsageManager> usageManagerProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  private DataRepository_Factory(Provider<AppDatabase> databaseProvider,
      Provider<DataUsageManager> usageManagerProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.databaseProvider = databaseProvider;
    this.usageManagerProvider = usageManagerProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public DataRepository get() {
    return newInstance(databaseProvider.get(), usageManagerProvider.get(), settingsDataStoreProvider.get());
  }

  public static DataRepository_Factory create(Provider<AppDatabase> databaseProvider,
      Provider<DataUsageManager> usageManagerProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new DataRepository_Factory(databaseProvider, usageManagerProvider, settingsDataStoreProvider);
  }

  public static DataRepository newInstance(AppDatabase database, DataUsageManager usageManager,
      SettingsDataStore settingsDataStore) {
    return new DataRepository(database, usageManager, settingsDataStore);
  }
}
