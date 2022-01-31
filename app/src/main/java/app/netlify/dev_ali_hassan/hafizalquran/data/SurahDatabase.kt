package app.netlify.dev_ali_hassan.hafizalquran.data;

import androidx.room.Database;
import androidx.room.RoomDatabase

/**
 * The database of the app to store all Quran needed info in it.
 */
@Database(entities = [Surah::class], version = 1)
abstract class SurahDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahsDao
}
