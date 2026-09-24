package io.arvo.dataconso.domain.usecase;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.DataUsageManager;
import io.arvo.dataconso.TelephonyRepository;
import io.arvo.dataconso.data.DataRepository;
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
public final class GetAppQuotasUseCase_Factory implements Factory<GetAppQuotasUseCase> {
  private final Provider<DataRepository> repositoryProvider;

  private final Provider<DataUsageManager> usageManagerProvider;

  private final Provider<TelephonyRepository> telephonyRepositoryProvider;

  private final Provider<Context> contextProvider;

  private GetAppQuotasUseCase_Factory(Provider<DataRepository> repositoryProvider,
      Provider<DataUsageManager> usageManagerProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.usageManagerProvider = usageManagerProvider;
    this.telephonyRepositoryProvider = telephonyRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public GetAppQuotasUseCase get() {
    return newInstance(repositoryProvider.get(), usageManagerProvider.get(), telephonyRepositoryProvider.get(), contextProvider.get());
  }

  public static GetAppQuotasUseCase_Factory create(Provider<DataRepository> repositoryProvider,
      Provider<DataUsageManager> usageManagerProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<Context> contextProvider) {
    return new GetAppQuotasUseCase_Factory(repositoryProvider, usageManagerProvider, telephonyRepositoryProvider, contextProvider);
  }

  public static GetAppQuotasUseCase newInstance(DataRepository repository,
      DataUsageManager usageManager, TelephonyRepository telephonyRepository, Context context) {
    return new GetAppQuotasUseCase(repository, usageManager, telephonyRepository, context);
  }
}
