package io.arvo.dataconso.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.ArvoAiEngine;
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
public final class GetAiInsightsUseCase_Factory implements Factory<GetAiInsightsUseCase> {
  private final Provider<ArvoAiEngine> aiEngineProvider;

  private final Provider<DataRepository> repositoryProvider;

  private GetAiInsightsUseCase_Factory(Provider<ArvoAiEngine> aiEngineProvider,
      Provider<DataRepository> repositoryProvider) {
    this.aiEngineProvider = aiEngineProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetAiInsightsUseCase get() {
    return newInstance(aiEngineProvider.get(), repositoryProvider.get());
  }

  public static GetAiInsightsUseCase_Factory create(Provider<ArvoAiEngine> aiEngineProvider,
      Provider<DataRepository> repositoryProvider) {
    return new GetAiInsightsUseCase_Factory(aiEngineProvider, repositoryProvider);
  }

  public static GetAiInsightsUseCase newInstance(ArvoAiEngine aiEngine, DataRepository repository) {
    return new GetAiInsightsUseCase(aiEngine, repository);
  }
}
