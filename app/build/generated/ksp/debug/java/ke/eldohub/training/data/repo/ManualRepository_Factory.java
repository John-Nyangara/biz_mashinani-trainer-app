package ke.eldohub.training.data.repo;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.local.ManualDao;
import ke.eldohub.training.data.remote.ContentRemoteSource;

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
    "deprecation"
})
public final class ManualRepository_Factory implements Factory<ManualRepository> {
  private final Provider<ManualDao> daoProvider;

  private final Provider<ContentRemoteSource> remoteSourceProvider;

  private final Provider<Context> contextProvider;

  public ManualRepository_Factory(Provider<ManualDao> daoProvider,
      Provider<ContentRemoteSource> remoteSourceProvider, Provider<Context> contextProvider) {
    this.daoProvider = daoProvider;
    this.remoteSourceProvider = remoteSourceProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public ManualRepository get() {
    return newInstance(daoProvider.get(), remoteSourceProvider.get(), contextProvider.get());
  }

  public static ManualRepository_Factory create(Provider<ManualDao> daoProvider,
      Provider<ContentRemoteSource> remoteSourceProvider, Provider<Context> contextProvider) {
    return new ManualRepository_Factory(daoProvider, remoteSourceProvider, contextProvider);
  }

  public static ManualRepository newInstance(ManualDao dao, ContentRemoteSource remoteSource,
      Context context) {
    return new ManualRepository(dao, remoteSource, context);
  }
}
