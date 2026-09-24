package io.arvo.dataconso;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.backend.CloudSyncManager;
import io.arvo.dataconso.data.DataRepository;
import io.arvo.dataconso.domain.usecase.GetAppQuotasUseCase;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<DataRepository> repositoryProvider;

  private final Provider<TelephonyRepository> telephonyRepositoryProvider;

  private final Provider<ArvoAiEngine> aiEngineProvider;

  private final Provider<DataUsageManager> usageManagerProvider;

  private final Provider<AppListManager> appManagerProvider;

  private final Provider<CloudSyncManager> cloudSyncProvider;

  private final Provider<GetAppQuotasUseCase> getAppQuotasUseCaseProvider;

  private final Provider<Context> contextProvider;

  private MainViewModel_Factory(Provider<DataRepository> repositoryProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<ArvoAiEngine> aiEngineProvider, Provider<DataUsageManager> usageManagerProvider,
      Provider<AppListManager> appManagerProvider, Provider<CloudSyncManager> cloudSyncProvider,
      Provider<GetAppQuotasUseCase> getAppQuotasUseCaseProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.telephonyRepositoryProvider = telephonyRepositoryProvider;
    this.aiEngineProvider = aiEngineProvider;
    this.usageManagerProvider = usageManagerProvider;
    this.appManagerProvider = appManagerProvider;
    this.cloudSyncProvider = cloudSyncProvider;
    this.getAppQuotasUseCaseProvider = getAppQuotasUseCaseProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(repositoryProvider.get(), telephonyRepositoryProvider.get(), aiEngineProvider.get(), usageManagerProvider.get(), appManagerProvider.get(), cloudSyncProvider.get(), getAppQuotasUseCaseProvider.get(), contextProvider.get());
  }

  public static MainViewModel_Factory create(Provider<DataRepository> repositoryProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<ArvoAiEngine> aiEngineProvider, Provider<DataUsageManager> usageManagerProvider,
      Provider<AppListManager> appManagerProvider, Provider<CloudSyncManager> cloudSyncProvider,
      Provider<GetAppQuotasUseCase> getAppQuotasUseCaseProvider,
      Provider<Context> contextProvider) {
    return new MainViewModel_Factory(repositoryProvider, telephonyRepositoryProvider, aiEngineProvider, usageManagerProvider, appManagerProvider, cloudSyncProvider, getAppQuotasUseCaseProvider, contextProvider);
  }

  public static MainViewModel newInstance(DataRepository repository,
      TelephonyRepository telephonyRepository, ArvoAiEngine aiEngine, DataUsageManager usageManager,
      AppListManager appManager, CloudSyncManager cloudSync,
      GetAppQuotasUseCase getAppQuotasUseCase, Context context) {
    return new MainViewModel(repository, telephonyRepository, aiEngine, usageManager, appManager, cloudSync, getAppQuotasUseCase, context);
  }
}
