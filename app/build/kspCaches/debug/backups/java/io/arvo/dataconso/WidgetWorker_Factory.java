package io.arvo.dataconso;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
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
public final class WidgetWorker_Factory {
  private final Provider<DataRepository> repositoryProvider;

  private final Provider<TelephonyRepository> telephonyRepositoryProvider;

  private final Provider<ArvoAiEngine> aiEngineProvider;

  private WidgetWorker_Factory(Provider<DataRepository> repositoryProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<ArvoAiEngine> aiEngineProvider) {
    this.repositoryProvider = repositoryProvider;
    this.telephonyRepositoryProvider = telephonyRepositoryProvider;
    this.aiEngineProvider = aiEngineProvider;
  }

  public WidgetWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, repositoryProvider.get(), telephonyRepositoryProvider.get(), aiEngineProvider.get());
  }

  public static WidgetWorker_Factory create(Provider<DataRepository> repositoryProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<ArvoAiEngine> aiEngineProvider) {
    return new WidgetWorker_Factory(repositoryProvider, telephonyRepositoryProvider, aiEngineProvider);
  }

  public static WidgetWorker newInstance(Context context, WorkerParameters params,
      DataRepository repository, TelephonyRepository telephonyRepository, ArvoAiEngine aiEngine) {
    return new WidgetWorker(context, params, repository, telephonyRepository, aiEngine);
  }
}
