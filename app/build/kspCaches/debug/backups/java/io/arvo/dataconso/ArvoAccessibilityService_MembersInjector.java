package io.arvo.dataconso;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import io.arvo.dataconso.data.AppDatabase;
import io.arvo.dataconso.data.DataRepository;
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
public final class ArvoAccessibilityService_MembersInjector implements MembersInjector<ArvoAccessibilityService> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<DataRepository> repositoryProvider;

  private ArvoAccessibilityService_MembersInjector(Provider<AppDatabase> databaseProvider,
      Provider<DataRepository> repositoryProvider) {
    this.databaseProvider = databaseProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public void injectMembers(ArvoAccessibilityService instance) {
    injectDatabase(instance, databaseProvider.get());
    injectRepository(instance, repositoryProvider.get());
  }

  public static MembersInjector<ArvoAccessibilityService> create(
      Provider<AppDatabase> databaseProvider, Provider<DataRepository> repositoryProvider) {
    return new ArvoAccessibilityService_MembersInjector(databaseProvider, repositoryProvider);
  }

  @InjectedFieldSignature("io.arvo.dataconso.ArvoAccessibilityService.database")
  public static void injectDatabase(ArvoAccessibilityService instance, AppDatabase database) {
    instance.database = database;
  }

  @InjectedFieldSignature("io.arvo.dataconso.ArvoAccessibilityService.repository")
  public static void injectRepository(ArvoAccessibilityService instance,
      DataRepository repository) {
    instance.repository = repository;
  }
}
