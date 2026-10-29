package ke.eldohub.training.ui.reports;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.local.ReportDao;

@ScopeMetadata
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
public final class ReportViewModel_Factory implements Factory<ReportViewModel> {
  private final Provider<ReportDao> reportDaoProvider;

  private final Provider<Context> contextProvider;

  public ReportViewModel_Factory(Provider<ReportDao> reportDaoProvider,
      Provider<Context> contextProvider) {
    this.reportDaoProvider = reportDaoProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public ReportViewModel get() {
    return newInstance(reportDaoProvider.get(), contextProvider.get());
  }

  public static ReportViewModel_Factory create(Provider<ReportDao> reportDaoProvider,
      Provider<Context> contextProvider) {
    return new ReportViewModel_Factory(reportDaoProvider, contextProvider);
  }

  public static ReportViewModel newInstance(ReportDao reportDao, Context context) {
    return new ReportViewModel(reportDao, context);
  }
}
