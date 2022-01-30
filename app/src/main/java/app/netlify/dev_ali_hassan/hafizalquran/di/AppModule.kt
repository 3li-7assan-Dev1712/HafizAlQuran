package app.netlify.dev_ali_hassan.hafizalquran.di

import android.app.Application
import androidx.room.Room
import app.netlify.dev_ali_hassan.hafizalquran.data.SurahDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSurahDatabase(app: Application) =
        Room.databaseBuilder(app, SurahDatabase::class.java, "surah_database")
            .fallbackToDestructiveMigration().build()

    @Provides
    fun provideSurahDao(surahDatabase: SurahDatabase) =
        surahDatabase.surahDao()
}