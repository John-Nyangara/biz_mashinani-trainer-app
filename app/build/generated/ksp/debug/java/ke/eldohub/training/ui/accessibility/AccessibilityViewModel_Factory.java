package ke.eldohub.training.ui.accessibility;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
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
public final class AccessibilityViewModel_Factory implements Factory<AccessibilityViewModel> {
  private final Provider<SpeechManager> speechManagerProvider;

  public AccessibilityViewModel_Factory(Provider<SpeechManager> speechManagerProvider) {
    this.speechManagerProvider = speechManagerProvider;
  }

  @Override
  public AccessibilityViewModel get() {
    return newInstance(speechManagerProvider.get());
  }

  public static AccessibilityViewModel_Factory create(
      Provider<SpeechManager> speechManagerProvider) {
    return new AccessibilityViewModel_Factory(speechManagerProvider);
  }

  public static AccessibilityViewModel newInstance(SpeechManager speechManager) {
    return new AccessibilityViewModel(speechManager);
  }
}
