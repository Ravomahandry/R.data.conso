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
public final class GetDashboardDataUseCase_Factory implements Factory<GetDashboardDataUseCase> {
  private final Provider<DataRepository> repositoryProvider;

  private final Provider<GetUsageUseCase> getUsageUseCaseProvider;

  private GetDashboardDataUseCase_Factory(Provider<DataRepository> repositoryProvider,
      Provider<GetUsageUseCase> getUsageUseCaseProvider) {
    this.repositoryProvider = repositoryProvider;
    this.getUsageUseCaseProvider = getUsageUseCaseProvider;
  }

  @Override
  public GetDashboardDataUseCase get() {
    return newInstance(repositoryProvider.get(), getUsageUseCaseProvider.get());
  }

  public static GetDashboardDataUseCase_Factory create(Provider<DataRepository> repositoryProvider,
      Provider<GetUsageUseCase> getUsageUseCaseProvider) {
    return new GetDashboardDataUseCase_Factory(repositoryProvider, getUsageUseCaseProvider);
  }

  public static GetDashboardDataUseCase newInstance(DataRepository repository,
      GetUsageUseCase getUsageUseCase) {
    return new GetDashboardDataUseCase(repository, getUsageUseCase);
  }
}
