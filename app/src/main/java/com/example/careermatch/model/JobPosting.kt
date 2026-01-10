package com.example.careermatch.model

import com.google.gson.annotations.SerializedName

// Tek bir iş ilanı.
data class JobPosting(
    @SerializedName("id") val id: String,

    @SerializedName("title") val title: String,

    @SerializedName("organization") val companyName: String?,

    @SerializedName("url") val jobUrl: String?,

    @SerializedName("location") val location: String? = null,

    // Yapay zeka için tam metin
    @SerializedName("description_text") val descriptionText: String?
)