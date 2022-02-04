package app.netlify.dev_ali_hassan.hafizalquran.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


/*Pojo class of the Surah.*/
@Entity(tableName = "suar")
@Parcelize
data class Surah (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahName: String,
    val isSurahMakia: Boolean,
    val surahPagesCount: Int,
    val surahState: Int = 1
    ) : Parcelable