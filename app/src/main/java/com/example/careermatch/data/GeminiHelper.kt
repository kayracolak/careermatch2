package com.example.careermatch.data

import com.example.careermatch.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiHelper {

    // Timeout ayarları
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiKey = BuildConfig.OPENAI_API_KEY

    suspend fun sendPromptToOpenAI(promptText: String): String {
        return withContext(Dispatchers.IO) {

            val json = JSONObject().apply {

                put("model", "gpt-4o-mini")

                val messagesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")

                        put("content", "Sen yardımcı bir yapay zeka asistanısın.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")

                        put("content", promptText)
                    })
                }
                put("messages", messagesArray)
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful) {
                        return@withContext "OpenAI Hatası (${response.code}): $responseBody"
                    }

                    if (responseBody == null) return@withContext "OpenAI boş cevap döndürdü."

                    val jsonResponse = JSONObject(responseBody)

                    val content = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    content
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Bağlantı hatası: ${e.localizedMessage}"
            }
        }
    }
}