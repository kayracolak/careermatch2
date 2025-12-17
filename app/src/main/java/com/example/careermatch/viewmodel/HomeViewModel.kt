package com.example.careermatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careermatch.data.GeminiHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    // Az önce oluşturduğun GeminiHelper sınıfını burada çağırıyoruz
    private val geminiHelper = GeminiHelper()

    // Ekranda göstereceğimiz Analiz Sonucu (Başta boş olabilir)
    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    // Yükleniyor animasyonu için durum (Loading)
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // Bu fonksiyon butona basılınca çalışacak
    fun analyzeUserTranscript() {
        val uid = auth.currentUser?.uid ?: return

        _loading.value = true
        _analysisResult.value = "Transkriptin inceleniyor, Yapay Zeka senin için düşünüyor..."

        // 1. Adım: Veritabanına git ve kayıtlı transkript metnini al
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Kaydettiğimiz "transcriptText" alanını çekiyoruz
                    val transcriptText = document.getString("transcriptText")

                    if (!transcriptText.isNullOrEmpty()) {
                        // 2. Adım: Metni bulduk, şimdi Gemini'ye gönderelim
                        viewModelScope.launch {
                            // GeminiHelper'daki fonksiyonu çağırıyoruz
                            val result = geminiHelper.analyzeTranscript(transcriptText)

                            // 3. Adım: Gelen cevabı ekrana yansıtıyoruz
                            _analysisResult.value = result
                            _loading.value = false
                        }
                    } else {
                        _analysisResult.value = "Hata: Sistemde transkript metni bulunamadı. Lütfen tekrar PDF yükleyin."
                        _loading.value = false
                    }
                }
            }
            .addOnFailureListener {
                _analysisResult.value = "Veri çekme hatası: ${it.localizedMessage}"
                _loading.value = false
            }
    }
}