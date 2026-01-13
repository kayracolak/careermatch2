package com.example.careermatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careermatch.data.GeminiHelper
import com.example.careermatch.model.JobPosting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedJobsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val openAI = GeminiHelper()

    // Kaydedilen ilanların listesi
    private val _savedJobs = MutableStateFlow<List<JobPosting>>(emptyList())
    val savedJobs = _savedJobs.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // Analiz durumları
    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult = _analysisResult.asStateFlow()
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    init {
        fetchSavedJobs()
    }

    // Favoriler
    private fun fetchSavedJobs() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true

        db.collection("users").document(uid).collection("savedJobs")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _loading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val jobs = snapshot.toObjects(JobPosting::class.java)
                    _savedJobs.value = jobs
                }
                _loading.value = false
            }
    }

    // Favoriden Kaldır
    fun removeJob(jobId: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("savedJobs").document(jobId).delete()
    }

    fun analyzeJobCompatibility(jobDescription: String) {
        val uid = auth.currentUser?.uid ?: return
        _isAnalyzing.value = true
        _analysisResult.value = null

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val transcriptText = document.getString("transcriptText")
                val extraInfoText = document.getString("extraInfo") ?: ""

                if (!transcriptText.isNullOrEmpty()) {
                    viewModelScope.launch {
                        val combinedPrompt = """
                            SEN KIDEMLİ BİR İNSAN KAYNAKLARI VE TEKNİK İŞE ALIM UZMANISIN.
                            GÖREVİN: Aşağıdaki iş ilanı ile adayın profilini analiz etmek.
                            İŞ İLANI: $jobDescription
                            🎓 ÖĞRENCİ TRANSKRİPTİ: $transcriptText
                            ✍️ ADAYIN EKSTRA BEYANI: ${extraInfoText.ifBlank { "Yok" }}
                            ÇIKTI FORMATI:
                            SCORE: [0-100 arası sayı]
                            BAŞLIK: Kariyer Uyumluluk Raporu
                            BÖLÜM 1: 🌟 Genel Değerlendirme
                            BÖLÜM 2: ✅ Temel Yetkinlik Eşleşmeleri
                            BÖLÜM 3: ⚠️ Gelişim Alanları & Eksikler
                            BÖLÜM 4: 💡 Kariyer Tavsiyesi
                            NOT: Markdown yıldız işaretlerini kullanma.
                        """.trimIndent()

                        val result = openAI.sendPromptToOpenAI(combinedPrompt)
                        _analysisResult.value = result
                        _isAnalyzing.value = false
                    }
                } else {
                    _analysisResult.value = "Transkript bulunamadı."
                    _isAnalyzing.value = false
                }
            }
            .addOnFailureListener {
                _analysisResult.value = "Hata oluştu."
                _isAnalyzing.value = false
            }
    }

    fun clearAnalysis() { _analysisResult.value = null }
}