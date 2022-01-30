package app.netlify.dev_ali_hassan.hafizalquran.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahsDao {
    /*the function is responsible for getting all the suar from the database*/
    @Query("SELECT * FROM suar ORDER BY surahNumber ASC")
    fun getAllSuar(): Flow<List<Surah>>

    /*when the usr saved a surah or feel they need to memorize it, then this function
    * will update the Surah state in the database.*/
    @Update
    suspend fun updateSurahState(updatedSurah: Surah)
}