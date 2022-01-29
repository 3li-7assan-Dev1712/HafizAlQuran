package app.netlify.dev_ali_hassan.hafizalquran.data;

import androidx.room.Database;
import androidx.room.RoomDatabase

@Database(entities = [Surah::class], version = 1)
abstract class SurahDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahsDao
}
