package app.netlify.dev_ali_hassan.hafizalquran.api

import app.netlify.dev_ali_hassan.hafizalquran.api.dataclasses.QuranApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface QuranApi {


    // this method will get the response from the Quran server
    @GET("v1/page/{pageNumber}/ar.alafasy")
    suspend fun getPageByNumber(
        @Path("pageNumber") pageNumber: Int
    ): Response<QuranApiResponse>

}