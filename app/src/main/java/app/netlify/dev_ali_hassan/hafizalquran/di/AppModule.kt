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
    * the database file is stored in the assest folder.
    *
    * no need to use fallbackToDescriptiveMigration method because this method
    * make room rewrite the data over and over again from the asset folder, and since we just
    * need to use the prepopulate data from the asset folder only one time at the installation time.*/
    @Singleton
    @Provides
    fun provideSurahDatabase(app: Application) =
        Room.databaseBuilder(app, SurahDatabase::class.java, "surah_database")
            .createFromAsset("database/surah_database.db")
            .build()

    @Provides
    fun provideSurahDao(surahDatabase: SurahDatabase) =
        surahDatabase.surahDao()

    @Provides
    fun providePageDao(surahDatabase: SurahDatabase) =
        surahDatabase.pageDao()
}