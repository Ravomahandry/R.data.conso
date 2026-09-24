package io.arvo.dataconso;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class UsageWorker2_AssistedFactory_Impl implements UsageWorker2_AssistedFactory {
  private final UsageWorker2_Factory delegateFactory;

  UsageWorker2_AssistedFactory_Impl(UsageWorker2_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public UsageWorker2 create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<UsageWorker2_AssistedFactory> create(
      UsageWorker2_Factory delegateFactory) {
    return InstanceFactory.create(new UsageWorker2_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<UsageWorker2_AssistedFactory> createFactoryProvider(
      UsageWorker2_Factory delegateFactory) {
    return InstanceFactory.create(new UsageWorker2_AssistedFactory_Impl(delegateFactory));
  }
}
