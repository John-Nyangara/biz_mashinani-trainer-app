package ke.eldohub.training.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "deprecation"
})
public final class ContentSyncWorker_AssistedFactory_Impl implements ContentSyncWorker_AssistedFactory {
  private final ContentSyncWorker_Factory delegateFactory;

  ContentSyncWorker_AssistedFactory_Impl(ContentSyncWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ContentSyncWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ContentSyncWorker_AssistedFactory> create(
      ContentSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ContentSyncWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ContentSyncWorker_AssistedFactory> createFactoryProvider(
      ContentSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ContentSyncWorker_AssistedFactory_Impl(delegateFactory));
  }
}
