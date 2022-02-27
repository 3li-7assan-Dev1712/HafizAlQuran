package app.netlify.dev_ali_hassan.hafizalquran.api

import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.QuranApiResponse
import retrofit2.Response

interface QuranApiHelper {
    suspend fun getPageByNumber(pageNumber: Int): Response<QuranApiResponse>
}