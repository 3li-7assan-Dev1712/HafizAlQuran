package app.netlify.dev_ali_hassan.hafizalquran.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* Surah root to indicate whether the surah is Makia or Medania
* */
enum class SurahRoot{
    MAKIA, MEDANIA
}
/*
* Surah state to let the user know whether they saved this surah, need some revision
* on it or not saved yet.*/
enum class SurahState {
    SAVED, NEED_REVISION, NOT_SAVED
}
/*Pojo class of the Surah.*/
@Entity(tableName = "suar")
data class Surah (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahName: String,
    val surahRoot: SurahRoot,
    val surahNumber: Int,
    val surahPagesCount: Int,
    val surahState: SurahState = SurahState.NOT_SAVED
    )