package io.arvo.dataconso;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.analytics.FirebaseAnalytics;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideApplicationFactory;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import io.arvo.dataconso.ai.ConsumptionPredictor;
import io.arvo.dataconso.backend.CloudSyncManager;
import io.arvo.dataconso.backend.CloudSyncModule_ProvideFirebaseAnalyticsFactory;
import io.arvo.dataconso.data.AppDatabase;
import io.arvo.dataconso.data.DataRepository;
import io.arvo.dataconso.data.SettingsDataStore;
import io.arvo.dataconso.di.AppModule_ProvideDataUsageManagerFactory;
import io.arvo.dataconso.di.DatabaseModule_ProvideDatabaseFactory;
import io.arvo.dataconso.di.NetworkModule_ProvideDnsResolverFactory;
import io.arvo.dataconso.di.NetworkModule_ProvideRealPacketInterceptorFactory;
import io.arvo.dataconso.domain.usecase.GetAppQuotasUseCase;
import io.arvo.dataconso.domain.usecase.GetDashboardDataUseCase;
import io.arvo.dataconso.domain.usecase.GetUsageUseCase;
import io.arvo.dataconso.network.DnsResolver;
import io.arvo.dataconso.network.RealPacketInterceptor;
import io.arvo.dataconso.ui.dashboard.DashboardViewModel;
import io.arvo.dataconso.ui.dashboard.DashboardViewModel_HiltModules;
import io.arvo.dataconso.ui.dashboard.DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.arvo.dataconso.ui.dashboard.DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import io.arvo.dataconso.ui.onboarding.PermissionViewModel;
import io.arvo.dataconso.ui.onboarding.PermissionViewModel_HiltModules;
import io.arvo.dataconso.ui.onboarding.PermissionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import io.arvo.dataconso.ui.onboarding.PermissionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerArvoApplication_HiltComponents_SingletonC {
  private DaggerArvoApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public ArvoApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements ArvoApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public ArvoApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements ArvoApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public ArvoApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements ArvoApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public ArvoApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements ArvoApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ArvoApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements ArvoApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ArvoApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements ArvoApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public ArvoApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements ArvoApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public ArvoApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends ArvoApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends ArvoApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends ArvoApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends ArvoApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>of(DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DashboardViewModel_HiltModules.KeyModule.provide(), MainViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MainViewModel_HiltModules.KeyModule.provide(), PermissionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PermissionViewModel_HiltModules.KeyModule.provide()));
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public void injectBlockOverlayActivity(BlockOverlayActivity blockOverlayActivity) {
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectCloudSyncManager(instance, singletonCImpl.cloudSyncManagerProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends ArvoApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<DashboardViewModel> dashboardViewModelProvider;

    Provider<MainViewModel> mainViewModelProvider;

    Provider<PermissionViewModel> permissionViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    GetUsageUseCase getUsageUseCase() {
      return new GetUsageUseCase(singletonCImpl.dataRepositoryProvider.get());
    }

    GetDashboardDataUseCase getDashboardDataUseCase() {
      return new GetDashboardDataUseCase(singletonCImpl.dataRepositoryProvider.get(), getUsageUseCase());
    }

    GetAppQuotasUseCase getAppQuotasUseCase() {
      return new GetAppQuotasUseCase(singletonCImpl.dataRepositoryProvider.get(), singletonCImpl.provideDataUsageManagerProvider.get(), singletonCImpl.telephonyRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.permissionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>of(DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (dashboardViewModelProvider)), MainViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (mainViewModelProvider)), PermissionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (permissionViewModelProvider))));
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // io.arvo.dataconso.ui.dashboard.DashboardViewModel
          return (T) new DashboardViewModel(viewModelCImpl.getDashboardDataUseCase(), singletonCImpl.consumptionPredictorProvider.get());

          case 1: // io.arvo.dataconso.MainViewModel
          return (T) new MainViewModel(singletonCImpl.dataRepositoryProvider.get(), singletonCImpl.telephonyRepositoryProvider.get(), singletonCImpl.arvoAiEngineProvider.get(), singletonCImpl.provideDataUsageManagerProvider.get(), singletonCImpl.appListManagerProvider.get(), singletonCImpl.cloudSyncManagerProvider.get(), viewModelCImpl.getAppQuotasUseCase(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // io.arvo.dataconso.ui.onboarding.PermissionViewModel
          return (T) new PermissionViewModel(ApplicationContextModule_ProvideApplicationFactory.provideApplication(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends ArvoApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends ArvoApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectArvoAccessibilityService(ArvoAccessibilityService arvoAccessibilityService) {
      injectArvoAccessibilityService2(arvoAccessibilityService);
    }

    @Override
    public void injectVpnBlockService(VpnBlockService vpnBlockService) {
      injectVpnBlockService2(vpnBlockService);
    }

    @CanIgnoreReturnValue
    private ArvoAccessibilityService injectArvoAccessibilityService2(
        ArvoAccessibilityService instance) {
      ArvoAccessibilityService_MembersInjector.injectDatabase(instance, singletonCImpl.provideDatabaseProvider.get());
      ArvoAccessibilityService_MembersInjector.injectRepository(instance, singletonCImpl.dataRepositoryProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private VpnBlockService injectVpnBlockService2(VpnBlockService instance2) {
      VpnBlockService_MembersInjector.injectRepository(instance2, singletonCImpl.dataRepositoryProvider.get());
      VpnBlockService_MembersInjector.injectUsageManager(instance2, singletonCImpl.provideDataUsageManagerProvider.get());
      VpnBlockService_MembersInjector.injectTelephonyRepository(instance2, singletonCImpl.telephonyRepositoryProvider.get());
      VpnBlockService_MembersInjector.injectDatabase(instance2, singletonCImpl.provideDatabaseProvider.get());
      VpnBlockService_MembersInjector.injectPacketInterceptor(instance2, singletonCImpl.provideRealPacketInterceptorProvider.get());
      VpnBlockService_MembersInjector.injectDnsResolver(instance2, singletonCImpl.provideDnsResolverProvider.get());
      VpnBlockService_MembersInjector.injectCloudSync(instance2, singletonCImpl.cloudSyncManagerProvider.get());
      return instance2;
    }
  }

  private static final class SingletonCImpl extends ArvoApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<AppDatabase> provideDatabaseProvider;

    Provider<DataUsageManager> provideDataUsageManagerProvider;

    Provider<SettingsDataStore> settingsDataStoreProvider;

    Provider<DataRepository> dataRepositoryProvider;

    Provider<UsageWorker2_AssistedFactory> usageWorker2_AssistedFactoryProvider;

    Provider<TelephonyRepository> telephonyRepositoryProvider;

    Provider<ArvoAiEngine> arvoAiEngineProvider;

    Provider<WidgetWorker_AssistedFactory> widgetWorker_AssistedFactoryProvider;

    Provider<FirebaseAnalytics> provideFirebaseAnalyticsProvider;

    Provider<CloudSyncManager> cloudSyncManagerProvider;

    Provider<ConsumptionPredictor> consumptionPredictorProvider;

    Provider<AppListManager> appListManagerProvider;

    Provider<RealPacketInterceptor> provideRealPacketInterceptorProvider;

    Provider<DnsResolver> provideDnsResolverProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOfListenableWorker(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("io.arvo.dataconso.UsageWorker2", ((Provider) (usageWorker2_AssistedFactoryProvider)), "io.arvo.dataconso.WidgetWorker", ((Provider) (widgetWorker_AssistedFactoryProvider)));
    }

    HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOfListenableWorker());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.provideDataUsageManagerProvider = DoubleCheck.provider(new SwitchingProvider<DataUsageManager>(singletonCImpl, 3));
      this.settingsDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<SettingsDataStore>(singletonCImpl, 4));
      this.dataRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DataRepository>(singletonCImpl, 1));
      this.usageWorker2_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<UsageWorker2_AssistedFactory>(singletonCImpl, 0));
      this.telephonyRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<TelephonyRepository>(singletonCImpl, 6));
      this.arvoAiEngineProvider = DoubleCheck.provider(new SwitchingProvider<ArvoAiEngine>(singletonCImpl, 7));
      this.widgetWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<WidgetWorker_AssistedFactory>(singletonCImpl, 5));
      this.provideFirebaseAnalyticsProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAnalytics>(singletonCImpl, 9));
      this.cloudSyncManagerProvider = DoubleCheck.provider(new SwitchingProvider<CloudSyncManager>(singletonCImpl, 8));
      this.consumptionPredictorProvider = DoubleCheck.provider(new SwitchingProvider<ConsumptionPredictor>(singletonCImpl, 10));
      this.appListManagerProvider = DoubleCheck.provider(new SwitchingProvider<AppListManager>(singletonCImpl, 11));
      this.provideRealPacketInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<RealPacketInterceptor>(singletonCImpl, 12));
      this.provideDnsResolverProvider = DoubleCheck.provider(new SwitchingProvider<DnsResolver>(singletonCImpl, 13));
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @Override
    public void injectArvoApplication(ArvoApplication arvoApplication) {
      injectArvoApplication2(arvoApplication);
    }

    @Override
    public void injectArvoWidgetProvider(ArvoWidgetProvider arvoWidgetProvider) {
    }

    @Override
    public void injectBootReceiver(BootReceiver bootReceiver) {
      injectBootReceiver2(bootReceiver);
    }

    @CanIgnoreReturnValue
    private ArvoApplication injectArvoApplication2(ArvoApplication instance) {
      ArvoApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    @CanIgnoreReturnValue
    private BootReceiver injectBootReceiver2(BootReceiver instance2) {
      BootReceiver_MembersInjector.injectRepository(instance2, dataRepositoryProvider.get());
      return instance2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // io.arvo.dataconso.UsageWorker2_AssistedFactory
          return (T) new UsageWorker2_AssistedFactory() {
            @Override
            public UsageWorker2 create(Context context, WorkerParameters params) {
              return new UsageWorker2(context, params, singletonCImpl.dataRepositoryProvider.get());
            }
          };

          case 1: // io.arvo.dataconso.data.DataRepository
          return (T) new DataRepository(singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.provideDataUsageManagerProvider.get(), singletonCImpl.settingsDataStoreProvider.get());

          case 2: // io.arvo.dataconso.data.AppDatabase
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // io.arvo.dataconso.DataUsageManager
          return (T) AppModule_ProvideDataUsageManagerFactory.provideDataUsageManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // io.arvo.dataconso.data.SettingsDataStore
          return (T) new SettingsDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // io.arvo.dataconso.WidgetWorker_AssistedFactory
          return (T) new WidgetWorker_AssistedFactory() {
            @Override
            public WidgetWorker create(Context context2, WorkerParameters params2) {
              return new WidgetWorker(context2, params2, singletonCImpl.dataRepositoryProvider.get(), singletonCImpl.telephonyRepositoryProvider.get(), singletonCImpl.arvoAiEngineProvider.get());
            }
          };

          case 6: // io.arvo.dataconso.TelephonyRepository
          return (T) new TelephonyRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // io.arvo.dataconso.ArvoAiEngine
          return (T) new ArvoAiEngine(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.dataRepositoryProvider.get());

          case 8: // io.arvo.dataconso.backend.CloudSyncManager
          return (T) new CloudSyncManager(singletonCImpl.dataRepositoryProvider.get(), singletonCImpl.provideFirebaseAnalyticsProvider.get());

          case 9: // com.google.firebase.analytics.FirebaseAnalytics
          return (T) CloudSyncModule_ProvideFirebaseAnalyticsFactory.provideFirebaseAnalytics(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // io.arvo.dataconso.ai.ConsumptionPredictor
          return (T) new ConsumptionPredictor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 11: // io.arvo.dataconso.AppListManager
          return (T) new AppListManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // io.arvo.dataconso.network.RealPacketInterceptor
          return (T) NetworkModule_ProvideRealPacketInterceptorFactory.provideRealPacketInterceptor();

          case 13: // io.arvo.dataconso.network.DnsResolver
          return (T) NetworkModule_ProvideDnsResolverFactory.provideDnsResolver(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
