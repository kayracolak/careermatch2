package com.example.careermatch.model

import com.google.gson.annotations.SerializedName

// API direkt olarak bir Liste döndürüyor, o yüzden ana bir "Response" sınıfına gerek yok.
// Bu sınıf tek bir iş ilanını temsil eder.

data class JobPosting(
    @SerializedName("id") val id: String,

    @SerializedName("title") val title: String,

    @SerializedName("organization") val companyName: String?,

    // İşin linki (Başvur butonu için)
    @SerializedName("url") val jobUrl: String?,

    // KONUM: API bazen "locations_raw" içinde veriyor, biz basitleştirelim
    // Şimdilik string olarak alıyoruz, null gelirse "Remote" yazarız
    @SerializedName("location") val location: String? = null,

    // KRİTİK NOKTA: Yapay zeka için tam metin
    // API dökümanına göre bu alanın adı "description_text" veya "description"
    // (Senin description_type='text' parametrene cevaben gelir)
    @SerializedName("description_text") val descriptionText: String?
)