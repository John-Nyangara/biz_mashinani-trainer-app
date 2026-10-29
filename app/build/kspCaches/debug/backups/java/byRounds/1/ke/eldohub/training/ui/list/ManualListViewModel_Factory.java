package ke.eldohub.training.ui.list;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.repo.ManualRepository;
import ke.eldohub.training.util.NetworkHelper;

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
public final class ManualListViewModel_Factory implements Factory<ManualListViewModel> {
  private final Provider<ManualRepository> repositoryProvider;

  private final Provider<NetworkHelper> networkHelperProvider;

  public ManualListViewModel_Factory(Provider<ManualRepository> repositoryProvider,
      Provider<NetworkHelper> networkHelperProvider) {
    this.repositoryProvider = repositoryProvider;
    this.networkHelperProvider = networkHelperProvider;
  }

  @Override
  public ManualListViewModel get() {
    return newInstance(repositoryProvider.get(), networkHelperProvider.get());
  }

  public static ManualListViewModel_Factory create(Provider<ManualRepository> repositoryProvider,
      Provider<NetworkHelper> networkHelperProvider) {
    return new ManualListViewModel_Factory(repositoryProvider, networkHelperProvider);
  }

  public static ManualListViewModel newInstance(ManualRepository repository,
      NetworkHelper networkHelper) {
    return new ManualListViewModel(repository, networkHelper);
  }
}
