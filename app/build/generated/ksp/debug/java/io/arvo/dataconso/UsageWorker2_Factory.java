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
public final class UsageWorker2_Factory {
  private final Provider<DataRepository> repositoryProvider;

  private UsageWorker2_Factory(Provider<DataRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public UsageWorker2 get(Context context, WorkerParameters params) {
    return newInstance(context, params, repositoryProvider.get());
  }

  public static UsageWorker2_Factory create(Provider<DataRepository> repositoryProvider) {
    return new UsageWorker2_Factory(repositoryProvider);
  }

  public static UsageWorker2 newInstance(Context context, WorkerParameters params,
      DataRepository repository) {
    return new UsageWorker2(context, params, repository);
  }
}
