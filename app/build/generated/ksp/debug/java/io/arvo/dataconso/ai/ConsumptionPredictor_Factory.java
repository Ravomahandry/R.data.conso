package io.arvo.dataconso.ai;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ConsumptionPredictor_Factory implements Factory<ConsumptionPredictor> {
  private final Provider<Context> contextProvider;

  private ConsumptionPredictor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ConsumptionPredictor get() {
    return newInstance(contextProvider.get());
  }

  public static ConsumptionPredictor_Factory create(Provider<Context> contextProvider) {
    return new ConsumptionPredictor_Factory(contextProvider);
  }

  public static ConsumptionPredictor newInstance(Context context) {
    return new ConsumptionPredictor(context);
  }
}
