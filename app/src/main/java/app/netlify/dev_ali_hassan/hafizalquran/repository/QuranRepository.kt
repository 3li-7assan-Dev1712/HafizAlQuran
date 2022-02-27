package app.netlify.dev_ali_hassan.hafizalquran.repository

import app.netlify.dev_ali_hassan.hafizalquran.api.QuranApiHelper
import app.netlify.dev_ali_hassan.hafizalquran.data.daos.PageDao
import javax.inject.Inject


class QuranRepository @Inject constructor(
    val pageDao: PageDao,
    val quranApiHelper: QuranApiHelper
) {
    suspend fun getPageOfNumber(pageNumber: Int) =
        quranApiHelper.getPageByNumber(pageNumber)
}