package ke.eldohub.training.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.local.AppDatabase;
import ke.eldohub.training.data.local.ReportDao;

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
public final class DatabaseModule_ProvideReportDaoFactory implements Factory<ReportDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideReportDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ReportDao get() {
    return provideReportDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideReportDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideReportDaoFactory(dbProvider);
  }

  public static ReportDao provideReportDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideReportDao(db));
  }
}
