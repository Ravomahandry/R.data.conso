package io.arvo.dataconso.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class GenerateAiStrategyUseCase_Factory implements Factory<GenerateAiStrategyUseCase> {
  @Override
  public GenerateAiStrategyUseCase get() {
    return newInstance();
  }

  public static GenerateAiStrategyUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GenerateAiStrategyUseCase newInstance() {
    return new GenerateAiStrategyUseCase();
  }

  private static final class InstanceHolder {
    static final GenerateAiStrategyUseCase_Factory INSTANCE = new GenerateAiStrategyUseCase_Factory();
  }
}
