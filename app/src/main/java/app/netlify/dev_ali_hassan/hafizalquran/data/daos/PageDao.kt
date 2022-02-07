package app.netlify.dev_ali_hassan.hafizalquran.data.daos

import androidx.room.Dao
import androidx.room.Query
import app.netlify.dev_ali_hassan.hafizalquran.data.models.Page
import kotlinx.coroutines.flow.Flow


@Dao
interface PageDao {
    @Query("SELECT * FROM page WHERE surahIdPageIn = :surahId ORDER BY surahIdPageIn")
    fun getSurahById(surahId: Int): Flow<List<Page>>
}