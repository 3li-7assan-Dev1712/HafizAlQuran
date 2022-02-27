package app.netlify.dev_ali_hassan.hafizalquran.api

import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.QuranApiResponse
import retrofit2.Response
import javax.inject.Inject

class QuranApiHelperImpl @Inject constructor(
    private val quranApi: QuranApi
) : QuranApiHelper {
    override suspend fun getPageByNumber(pageNumber: Int): Response<QuranApiResponse> =
        quranApi.getPageByNumber(pageNumber)
}