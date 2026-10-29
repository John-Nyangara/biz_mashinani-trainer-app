package ke.eldohub.training;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class EldoHubApp_MembersInjector implements MembersInjector<EldoHubApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public EldoHubApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<EldoHubApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new EldoHubApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(EldoHubApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("ke.eldohub.training.EldoHubApp.workerFactory")
  public static void injectWorkerFactory(EldoHubApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
