package com.example.careermatch.model

import com.google.gson.annotations.SerializedName

data class JobPosting(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("organization") val companyName: String? = null,
    @SerializedName("url") val jobUrl: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("description_text") val descriptionText: String? = null
)