package app.netlify.dev_ali_hassan.hafizalquran.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "page")
data class Page(
    val pageText: String = "",
    val surahIdPageIn: Int,
    val pageNumber: Int,
    val pageIsMemorized: Boolean,
    val isDownloaded: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 1

): Parcelable