package io.arvo.dataconso.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.data.DataRepository;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class GetSyncedHistoryUseCase_Factory implements Factory<GetSyncedHistoryUseCase> {
  private final Provider<DataRepository> repositoryProvider;

  private GetSyncedHistoryUseCase_Factory(Provider<DataRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetSyncedHistoryUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetSyncedHistoryUseCase_Factory create(
      Provider<DataRepository> repositoryProvider) {
    return new GetSyncedHistoryUseCase_Factory(repositoryProvider);
  }

  public static GetSyncedHistoryUseCase newInstance(DataRepository repository) {
    return new GetSyncedHistoryUseCase(repository);
  }
}
