package io.arvo.dataconso;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import io.arvo.dataconso.data.DataRepository;
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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<DataRepository> repositoryProvider;

  private BootReceiver_MembersInjector(Provider<DataRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  public static MembersInjector<BootReceiver> create(Provider<DataRepository> repositoryProvider) {
    return new BootReceiver_MembersInjector(repositoryProvider);
  }

  @InjectedFieldSignature("io.arvo.dataconso.BootReceiver.repository")
  public static void injectRepository(BootReceiver instance, DataRepository repository) {
    instance.repository = repository;
  }
}
