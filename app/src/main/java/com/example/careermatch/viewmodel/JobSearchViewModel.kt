package com.example.careermatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careermatch.data.GeminiHelper
import com.example.careermatch.data.RetrofitClient
import com.example.careermatch.model.JobPosting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobSearchViewModel : ViewModel() {

    // Servisler
    private val api = RetrofitClient.linkedInApi
    private val openAI = GeminiHelper() // İsmi GeminiHelper kaldı ama içi OpenAI :)
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 1. Durumlar (State) - İş Listesi
    private val _jobs = MutableStateFlow<List<JobPosting>>(emptyList())
    val jobs = _jobs.asStateFlow()

    // 2. Durumlar - Yükleniyor mu?
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // 3. Durumlar - Hata Mesajı
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // 4. Durumlar - Analiz Sonucu (Bottom Sheet için)
    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    // FONKSİYON 1: İş Ara
    fun searchJobs(title: String, location: String) {
        if (title.isBlank() || location.isBlank()) {
            _error.value = "Lütfen iş unvanı ve konum giriniz."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // API İsteği (Local.properties'deki key otomatik gidecek)
                val results = api.searchJobs(
                    apiKey = com.example.careermatch.BuildConfig.RAPID_API_KEY,
                    jobTitle = title,
                    location = location
                )
                _jobs.value = results
            } catch (e: Exception) {
                _error.value = "Arama hatası: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    // FONKSİYON 2: Seçilen İşi Analiz Et
    fun analyzeJobCompatibility(jobDescription: String) {
        val uid = auth.currentUser?.uid ?: return

        _isAnalyzing.value = true
        _analysisResult.value = null // Önceki sonucu temizle

        // 1. Önce Firestore'dan Transkripti çek
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val transcriptText = document.getString("transcriptText")

                if (!transcriptText.isNullOrEmpty()) {
                    // 2. Transkript var, şimdi İlan Metni ile birleştirip AI'ya soralım
                    viewModelScope.launch {
                        // AI'ya özel bir prompt hazırlayalım
                        val combinedPrompt = """
                            AŞAĞIDAKİ İŞ İLANINI VE ÖĞRENCİ TRANSKRİPTİNİ KIYASLA.
                            
                            İŞ İLANI DETAYLARI:
                            $jobDescription
                            
                            ÖĞRENCİ TRANSKRİPTİ:
                            $transcriptText
                            
                            GÖREVİN:
                            Bu öğrenci bu işe ne kadar uygun? 
                            1. %0 ile %100 arası bir UYUM SKORU ver.
                            2. Eksik olduğu kritik yetenekleri söyle.
                            3. Avantajlı olduğu dersleri/konuları söyle.
                            4. Kısa ve net ol.
                        """.trimIndent()

                        val result = openAI.sendPromptToOpenAI(combinedPrompt)

                        _analysisResult.value = result
                        _isAnalyzing.value = false
                    }
                } else {
                    _analysisResult.value = "Transkript bulunamadı. Lütfen önce transkript yükleyin."
                    _isAnalyzing.value = false
                }
            }
            .addOnFailureListener {
                _analysisResult.value = "Veri hatası: ${it.localizedMessage}"
                _isAnalyzing.value = false
            }
    }

    // Analiz sonucunu kapatmak için (Bottom sheet kapanınca)
    fun clearAnalysis() {
        _analysisResult.value = null
    }
}