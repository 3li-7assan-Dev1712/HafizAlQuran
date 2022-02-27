package app.netlify.dev_ali_hassan.hafizalquran.di

import android.app.Application
import androidx.room.Room
import app.netlify.dev_ali_hassan.hafizalquran.BuildConfig
import app.netlify.dev_ali_hassan.hafizalquran.api.QuranApi
import app.netlify.dev_ali_hassan.hafizalquran.api.QuranApiHelper
import app.netlify.dev_ali_hassan.hafizalquran.api.QuranApiHelperImpl
import app.netlify.dev_ali_hassan.hafizalquran.data.SurahDatabase
import app.netlify.dev_ali_hassan.hafizalquran.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideBaseUrl(): String = Constants.BASE_URL


    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()


    @Provides
    @Singleton
    fun provideQuranApi(retrofit: Retrofit) =
        retrofit.create(QuranApi::class.java)

    @Provides
    @Singleton
    fun provideQuranApiHelper(apiHelperImpl: QuranApiHelperImpl): QuranApiHelper =
        apiHelperImpl

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