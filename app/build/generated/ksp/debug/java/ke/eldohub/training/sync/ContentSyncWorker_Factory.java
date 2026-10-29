package ke.eldohub.training.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.repo.ManualRepository;

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
    "deprecation"
})
public final class ContentSyncWorker_Factory {
  private final Provider<ManualRepository> repositoryProvider;

  public ContentSyncWorker_Factory(Provider<ManualRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public ContentSyncWorker get(Context appContext, WorkerParameters params) {
    return newInstance(appContext, params, repositoryProvider.get());
  }

  public static ContentSyncWorker_Factory create(Provider<ManualRepository> repositoryProvider) {
    return new ContentSyncWorker_Factory(repositoryProvider);
  }

  public static ContentSyncWorker newInstance(Context appContext, WorkerParameters params,
      ManualRepository repository) {
    return new ContentSyncWorker(appContext, params, repository);
  }
}
