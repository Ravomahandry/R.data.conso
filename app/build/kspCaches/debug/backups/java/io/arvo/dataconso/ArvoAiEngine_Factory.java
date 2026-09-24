package io.arvo.dataconso;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.data.AppDatabase;
import io.arvo.dataconso.data.DataRepository;
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
public final class ArvoAiEngine_Factory implements Factory<ArvoAiEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<AppDatabase> databaseProvider;

  private final Provider<DataRepository> repositoryProvider;

  private ArvoAiEngine_Factory(Provider<Context> contextProvider,
      Provider<AppDatabase> databaseProvider, Provider<DataRepository> repositoryProvider) {
    this.contextProvider = contextProvider;
    this.databaseProvider = databaseProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ArvoAiEngine get() {
    return newInstance(contextProvider.get(), databaseProvider.get(), repositoryProvider.get());
  }

  public static ArvoAiEngine_Factory create(Provider<Context> contextProvider,
      Provider<AppDatabase> databaseProvider, Provider<DataRepository> repositoryProvider) {
    return new ArvoAiEngine_Factory(contextProvider, databaseProvider, repositoryProvider);
  }

  public static ArvoAiEngine newInstance(Context context, AppDatabase database,
      DataRepository repository) {
    return new ArvoAiEngine(context, database, repository);
  }
}
