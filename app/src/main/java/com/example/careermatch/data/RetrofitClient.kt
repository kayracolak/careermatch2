package com.example.careermatch.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://linkedin-job-search-api.p.rapidapi.com/"

    // LinkedIn API
    val linkedInApi: LinkedInApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LinkedInApi::class.java)
    }
}