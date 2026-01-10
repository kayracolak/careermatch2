package com.example.careermatch.data

import com.example.careermatch.model.JobPosting
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LinkedInApi {

    // Son 7 günde yayınlanan aktif işleri getirir
    @GET("active-jb-7d")
    suspend fun searchJobs(
        // API KEY'i Header olarak göndereceğiz (BuildConfig'den gelecek)
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") host: String = "linkedin-job-search-api.p.rapidapi.com",

        // Senin istediğin filtreler:
        @Query("title_filter") jobTitle: String, // Örn: "iOS Developer"
        @Query("location_filter") location: String, // Örn: "Turkey"

        // ÖNEMLİ: Tam metin gelmesi için bunu sabit "text" yapıyoruz
        @Query("description_type") descType: String = "text",

        // Sayfalama ve Limit
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): List<JobPosting> // Cevap direkt liste olarak dönüyor
}