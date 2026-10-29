package ke.eldohub.training.util;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SpeechManager_Factory implements Factory<SpeechManager> {
  private final Provider<Context> contextProvider;

  public SpeechManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SpeechManager get() {
    return newInstance(contextProvider.get());
  }

  public static SpeechManager_Factory create(Provider<Context> contextProvider) {
    return new SpeechManager_Factory(contextProvider);
  }

  public static SpeechManager newInstance(Context context) {
    return new SpeechManager(context);
  }
}
