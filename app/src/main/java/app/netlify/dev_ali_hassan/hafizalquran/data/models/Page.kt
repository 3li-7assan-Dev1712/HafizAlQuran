package app.netlify.dev_ali_hassan.hafizalquran.data.models

import androidx.room.Entity

@Entity(tableName = "page")
data class Page(
    val surahIdPageIn: Int,
    val pageNumber: Int,
    val pageIsMemorized: Boolean
) {
}