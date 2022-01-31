package app.netlify.dev_ali_hassan.hafizalquran.data

import androidx.room.Entity
import androidx.room.PrimaryKey


/*Pojo class of the Surah.*/
@Entity(tableName = "suar")
data class Surah (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahName: String,
    val isSurahMakia: Boolean,
    val surahPagesCount: Int,
    val surahState: Int = 1
    )