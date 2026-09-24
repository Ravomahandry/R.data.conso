package io.arvo.dataconso;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class ArvoApplication_MembersInjector implements MembersInjector<ArvoApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private ArvoApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  @Override
  public void injectMembers(ArvoApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  public static MembersInjector<ArvoApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new ArvoApplication_MembersInjector(workerFactoryProvider);
  }

  @InjectedFieldSignature("io.arvo.dataconso.ArvoApplication.workerFactory")
  public static void injectWorkerFactory(ArvoApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
