package io.arvo.dataconso;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = WidgetWorker.class
)
public interface WidgetWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("io.arvo.dataconso.WidgetWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(WidgetWorker_AssistedFactory factory);
}
