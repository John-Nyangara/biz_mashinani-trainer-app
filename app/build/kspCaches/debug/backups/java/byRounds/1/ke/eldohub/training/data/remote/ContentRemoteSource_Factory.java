package ke.eldohub.training.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ContentRemoteSource_Factory implements Factory<ContentRemoteSource> {
  @Override
  public ContentRemoteSource get() {
    return newInstance();
  }

  public static ContentRemoteSource_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ContentRemoteSource newInstance() {
    return new ContentRemoteSource();
  }

  private static final class InstanceHolder {
    private static final ContentRemoteSource_Factory INSTANCE = new ContentRemoteSource_Factory();
  }
}
