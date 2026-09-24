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
public final class WidgetWorker_AssistedFactory_Impl implements WidgetWorker_AssistedFactory {
  private final WidgetWorker_Factory delegateFactory;

  WidgetWorker_AssistedFactory_Impl(WidgetWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public WidgetWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<WidgetWorker_AssistedFactory> create(
      WidgetWorker_Factory delegateFactory) {
    return InstanceFactory.create(new WidgetWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<WidgetWorker_AssistedFactory> createFactoryProvider(
      WidgetWorker_Factory delegateFactory) {
    return InstanceFactory.create(new WidgetWorker_AssistedFactory_Impl(delegateFactory));
  }
}
