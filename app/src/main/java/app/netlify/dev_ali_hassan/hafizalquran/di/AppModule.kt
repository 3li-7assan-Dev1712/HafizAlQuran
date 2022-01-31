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

    /*The function below will provide the database for the app with prepared data in it
    * the database file is stored in the assests folder.*/
    @Singleton
    @Provides
    fun provideSurahDatabase(app: Application) =
        Room.databaseBuilder(app, SurahDatabase::class.java, "surah_database")
            .fallbackToDestructiveMigration()
            .createFromAsset("database/surah_database.db")
            .build()

    @Provides
    fun provideSurahDao(surahDatabase: SurahDatabase) =
        surahDatabase.surahDao()
}