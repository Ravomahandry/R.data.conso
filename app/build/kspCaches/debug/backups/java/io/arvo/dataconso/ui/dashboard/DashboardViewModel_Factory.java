package io.arvo.dataconso.ui.dashboard;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.arvo.dataconso.ai.ConsumptionPredictor;
import io.arvo.dataconso.domain.usecase.GetDashboardDataUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<GetDashboardDataUseCase> getDashboardDataUseCaseProvider;

  private final Provider<ConsumptionPredictor> consumptionPredictorProvider;

  private DashboardViewModel_Factory(
      Provider<GetDashboardDataUseCase> getDashboardDataUseCaseProvider,
      Provider<ConsumptionPredictor> consumptionPredictorProvider) {
    this.getDashboardDataUseCaseProvider = getDashboardDataUseCaseProvider;
    this.consumptionPredictorProvider = consumptionPredictorProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(getDashboardDataUseCaseProvider.get(), consumptionPredictorProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<GetDashboardDataUseCase> getDashboardDataUseCaseProvider,
      Provider<ConsumptionPredictor> consumptionPredictorProvider) {
    return new DashboardViewModel_Factory(getDashboardDataUseCaseProvider, consumptionPredictorProvider);
  }

  public static DashboardViewModel newInstance(GetDashboardDataUseCase getDashboardDataUseCase,
      ConsumptionPredictor consumptionPredictor) {
    return new DashboardViewModel(getDashboardDataUseCase, consumptionPredictor);
  }
}
