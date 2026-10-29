package ke.eldohub.training.ui.dashboard;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ke.eldohub.training.data.repo.ManualRepository;
import ke.eldohub.training.util.NetworkHelper;
import ke.eldohub.training.util.SpeechManager;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<ManualRepository> manualRepositoryProvider;

  private final Provider<SpeechManager> speechManagerProvider;

  private final Provider<NetworkHelper> networkHelperProvider;

  public DashboardViewModel_Factory(Provider<ManualRepository> manualRepositoryProvider,
      Provider<SpeechManager> speechManagerProvider,
      Provider<NetworkHelper> networkHelperProvider) {
    this.manualRepositoryProvider = manualRepositoryProvider;
    this.speechManagerProvider = speechManagerProvider;
    this.networkHelperProvider = networkHelperProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(manualRepositoryProvider.get(), speechManagerProvider.get(), networkHelperProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<ManualRepository> manualRepositoryProvider,
      Provider<SpeechManager> speechManagerProvider,
      Provider<NetworkHelper> networkHelperProvider) {
    return new DashboardViewModel_Factory(manualRepositoryProvider, speechManagerProvider, networkHelperProvider);
  }

  public static DashboardViewModel newInstance(ManualRepository manualRepository,
      SpeechManager speechManager, NetworkHelper networkHelper) {
    return new DashboardViewModel(manualRepository, speechManager, networkHelper);
  }
}
