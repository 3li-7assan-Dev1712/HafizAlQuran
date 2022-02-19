package app.netlify.dev_ali_hassan.hafizalquran.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page")
data class Page(
    val surahIdPageIn: Int,
    val pageNumber: Int,
    val pageIsMemorized: Boolean,
    @PrimaryKey(autoGenerate = true) val id: Int = 1

)