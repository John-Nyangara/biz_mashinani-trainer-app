package ke.eldohub.training.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.eldohub.training.data.local.AppDatabase
import ke.eldohub.training.data.local.ManualDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "eldohub.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideManualDao(db: AppDatabase): ManualDao = db.manualDao()
}
