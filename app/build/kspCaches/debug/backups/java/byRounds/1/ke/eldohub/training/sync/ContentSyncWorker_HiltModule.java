package ke.eldohub.training.sync;

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
    topLevelClass = ContentSyncWorker.class
)
public interface ContentSyncWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("ke.eldohub.training.sync.ContentSyncWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(ContentSyncWorker_AssistedFactory factory);
}
