package com.example.careermatch.data

import com.example.careermatch.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

class GeminiHelper {

    private val client = OkHttpClient()
    private val apiKey = BuildConfig.OPENAI_API_KEY

    /**
     * Firestore'dan gelen transcriptText'i analiz eder
     * ve öğrenciye özel kariyer raporu döner
     */
    suspend fun analyzeTranscript(transcriptText: String): String {
        return withContext(Dispatchers.IO) {

            val prompt = """
                Sen üniversite öğrencilerine rehberlik eden samimi ve uzman bir kariyer danışmanısın.

                GÖREVİN:
                Aşağıdaki transkript metnini analiz et ve öğrenciye özel motive edici bir rapor hazırla.

                RAPOR FORMATI:
                1. 🌟 Güçlü Yönlerin
                2. 🚀 Gelişim Alanların
                3. 💼 Sana Uygun Kariyer Yolları (3 adet)

                TRANSKRİPT:
                $transcriptText
            """.trimIndent()

            // 🔹 OpenAI Responses API için doğru JSON
            val json = JSONObject().apply {
                put("model", "gpt-4.1-mini")
                put(
                    "input",
                    JSONArray().apply {
                        put(
                            JSONObject().apply {
                                put("role", "user")
                                put("content", prompt)
                            }
                        )
                    }
                )
            }

            val body = RequestBody.create(
                "application/json".toMediaType(),
                json.toString()
            )

            val request = Request.Builder()
                .url("https://api.openai.com/v1/responses")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            try {
                client.newCall(request).execute().use { response ->

                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        return@withContext "OpenAI Hatası (${response.code}): $errorBody"
                    }

                    val responseBody = response.body?.string()
                        ?: return@withContext "OpenAI boş cevap döndürdü."

                    val jsonResponse = JSONObject(responseBody)

                    // 🔹 Responses API doğru parse
                    jsonResponse
                        .getJSONArray("output")
                        .getJSONObject(0)
                        .getJSONArray("content")
                        .getJSONObject(0)
                        .getString("text")
                }
            } catch (e: Exception) {
                "OpenAI bağlantı hatası: ${e.localizedMessage}"
            }
        }
    }
    
}
