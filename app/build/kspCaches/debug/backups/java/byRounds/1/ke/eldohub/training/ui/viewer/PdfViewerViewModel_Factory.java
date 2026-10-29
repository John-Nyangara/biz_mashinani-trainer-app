package ke.eldohub.training.ui.viewer;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.repo.ManualRepository;

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
public final class PdfViewerViewModel_Factory implements Factory<PdfViewerViewModel> {
  private final Provider<ManualRepository> repositoryProvider;

  private final Provider<Context> contextProvider;

  public PdfViewerViewModel_Factory(Provider<ManualRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public PdfViewerViewModel get() {
    return newInstance(repositoryProvider.get(), contextProvider.get());
  }

  public static PdfViewerViewModel_Factory create(Provider<ManualRepository> repositoryProvider,
      Provider<Context> contextProvider) {
    return new PdfViewerViewModel_Factory(repositoryProvider, contextProvider);
  }

  public static PdfViewerViewModel newInstance(ManualRepository repository, Context context) {
    return new PdfViewerViewModel(repository, context);
  }
}
