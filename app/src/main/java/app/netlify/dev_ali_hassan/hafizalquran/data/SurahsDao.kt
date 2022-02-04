package app.netlify.dev_ali_hassan.hafizalquran.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Surah
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) our interface for providing convenient functions
 * to make transaction between the app and the database.
 */
@Dao
interface SurahsDao {
    /*the function is responsible for getting all the suar from the database*/
    @Query("SELECT * FROM suar ORDER BY id ASC")
    fun getAllSuar(): Flow<List<Surah>>

    /*when the usr saved a surah or feel they need to memorize it, then this function
    * will update the Surah state in the database.*/
    @Update
    suspend fun updateSurahState(updatedSurah: Surah)
}