package app.netlify.dev_ali_hassan.hafizalquran.data;

import androidx.room.Database;
import androidx.room.RoomDatabase
import app.netlify.dev_ali_hassan.hafizalquran.data.daos.PageDao
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page

/**
 * The database of the app to store all Quran needed info in it.
 */
@Database(entities = [Surah::class, Page::class], version = 4)
abstract class SurahDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahsDao
    abstract fun pageDao(): PageDao
}
