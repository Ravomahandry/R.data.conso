package io.arvo.dataconso.ui.onboarding;

import android.app.Application;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class PermissionViewModel_Factory implements Factory<PermissionViewModel> {
  private final Provider<Application> applicationProvider;

  private PermissionViewModel_Factory(Provider<Application> applicationProvider) {
    this.applicationProvider = applicationProvider;
  }

  @Override
  public PermissionViewModel get() {
    return newInstance(applicationProvider.get());
  }

  public static PermissionViewModel_Factory create(Provider<Application> applicationProvider) {
    return new PermissionViewModel_Factory(applicationProvider);
  }

  public static PermissionViewModel newInstance(Application application) {
    return new PermissionViewModel(application);
  }
}
