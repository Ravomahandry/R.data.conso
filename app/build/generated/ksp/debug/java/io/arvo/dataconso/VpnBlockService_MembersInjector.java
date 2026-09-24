package io.arvo.dataconso;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import io.arvo.dataconso.backend.CloudSyncManager;
import io.arvo.dataconso.data.AppDatabase;
import io.arvo.dataconso.data.DataRepository;
import io.arvo.dataconso.network.DnsResolver;
import io.arvo.dataconso.network.RealPacketInterceptor;
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
public final class VpnBlockService_MembersInjector implements MembersInjector<VpnBlockService> {
  private final Provider<DataRepository> repositoryProvider;

  private final Provider<DataUsageManager> usageManagerProvider;

  private final Provider<TelephonyRepository> telephonyRepositoryProvider;

  private final Provider<AppDatabase> databaseProvider;

  private final Provider<RealPacketInterceptor> packetInterceptorProvider;

  private final Provider<DnsResolver> dnsResolverProvider;

  private final Provider<CloudSyncManager> cloudSyncProvider;

  private VpnBlockService_MembersInjector(Provider<DataRepository> repositoryProvider,
      Provider<DataUsageManager> usageManagerProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<AppDatabase> databaseProvider,
      Provider<RealPacketInterceptor> packetInterceptorProvider,
      Provider<DnsResolver> dnsResolverProvider, Provider<CloudSyncManager> cloudSyncProvider) {
    this.repositoryProvider = repositoryProvider;
    this.usageManagerProvider = usageManagerProvider;
    this.telephonyRepositoryProvider = telephonyRepositoryProvider;
    this.databaseProvider = databaseProvider;
    this.packetInterceptorProvider = packetInterceptorProvider;
    this.dnsResolverProvider = dnsResolverProvider;
    this.cloudSyncProvider = cloudSyncProvider;
  }

  @Override
  public void injectMembers(VpnBlockService instance) {
    injectRepository(instance, repositoryProvider.get());
    injectUsageManager(instance, usageManagerProvider.get());
    injectTelephonyRepository(instance, telephonyRepositoryProvider.get());
    injectDatabase(instance, databaseProvider.get());
    injectPacketInterceptor(instance, packetInterceptorProvider.get());
    injectDnsResolver(instance, dnsResolverProvider.get());
    injectCloudSync(instance, cloudSyncProvider.get());
  }

  public static MembersInjector<VpnBlockService> create(Provider<DataRepository> repositoryProvider,
      Provider<DataUsageManager> usageManagerProvider,
      Provider<TelephonyRepository> telephonyRepositoryProvider,
      Provider<AppDatabase> databaseProvider,
      Provider<RealPacketInterceptor> packetInterceptorProvider,
      Provider<DnsResolver> dnsResolverProvider, Provider<CloudSyncManager> cloudSyncProvider) {
    return new VpnBlockService_MembersInjector(repositoryProvider, usageManagerProvider, telephonyRepositoryProvider, databaseProvider, packetInterceptorProvider, dnsResolverProvider, cloudSyncProvider);
  }

  @InjectedFieldSignature("io.arvo.dataconso.VpnBlockService.repository")
  public static void injectRepository(VpnBlockService instance, DataRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("io.arvo.dataconso.VpnBlockService.usageManager")
  public static void injectUsageManager(VpnBlockService instance, DataUsageManager usageManager) {
    instance.usageManager = usageManager;
  }

  @InjectedFieldSignature("io.arvo.dataconso.VpnBlockService.telephonyRepository")
  public static void injectTelephonyRepository(VpnBlockService instance,
      TelephonyRepository telephonyRepository) {
    instance.telephonyRepository = telephonyRepository;
  }

  @InjectedFieldSignature("io.arvo.dataconso.VpnBlockService.database")
  public static void injectDatabase(VpnBlockService instance, AppDatabase database) {
    instance.database = database;
  }

  @InjectedFieldSignature("io.arvo.dataconso.VpnBlockService.packetInterceptor")
  public static void injectPacketInterceptor(VpnBlockService instance,
      RealPacketInterceptor packetInterceptor) {
    instance.packetInterceptor = packetInterceptor;
  }

  @InjectedFieldSignature("io.arvo.dataconso.VpnBlockService.dnsResolver")
  public static void injectDnsResolver(VpnBlockService instance, DnsResolver dnsResolver) {
    instance.dnsResolver = dnsResolver;
  }

  @InjectedFieldSignature("io.arvo.dataconso.VpnBlockService.cloudSync")
  public static void injectCloudSync(VpnBlockService instance, CloudSyncManager cloudSync) {
    instance.cloudSync = cloudSync;
  }
}
