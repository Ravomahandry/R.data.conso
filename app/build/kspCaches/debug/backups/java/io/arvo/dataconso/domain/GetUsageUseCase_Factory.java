package io.arvo.dataconso.domain;

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
public final class GetUsageUseCase_Factory implements Factory<GetUsageUseCase> {
  private final Provider<DataRepository> repositoryProvider;

  private GetUsageUseCase_Factory(Provider<DataRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetUsageUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetUsageUseCase_Factory create(Provider<DataRepository> repositoryProvider) {
    return new GetUsageUseCase_Factory(repositoryProvider);
  }

  public static GetUsageUseCase newInstance(DataRepository repository) {
    return new GetUsageUseCase(repository);
  }
}
