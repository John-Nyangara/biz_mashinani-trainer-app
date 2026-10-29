package ke.eldohub.training.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.local.AppDatabase;
import ke.eldohub.training.data.local.ManualDao;

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
public final class DatabaseModule_ProvideManualDaoFactory implements Factory<ManualDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideManualDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ManualDao get() {
    return provideManualDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideManualDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideManualDaoFactory(dbProvider);
  }

  public static ManualDao provideManualDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideManualDao(db));
  }
}
