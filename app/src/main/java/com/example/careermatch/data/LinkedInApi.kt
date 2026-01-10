package com.example.careermatch.data

import com.example.careermatch.model.JobPosting
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LinkedInApi {

    // Son 7 gün
    @GET("active-jb-7d")
    suspend fun searchJobs(

        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") host: String = "linkedin-job-search-api.p.rapidapi.com",


        @Query("title_filter") jobTitle: String,
        @Query("location_filter") location: String,
        @Query("description_type") descType: String = "text",

        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): List<JobPosting>
}