package com.example.careermatch.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TranscriptViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Durumlar
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus = _uploadStatus.asStateFlow()

    private val _existingTranscriptUrl = MutableStateFlow<String?>(null)
    val existingTranscriptUrl = _existingTranscriptUrl.asStateFlow()

    init {
        checkExistingTranscript()
    }

    private fun checkExistingTranscript() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val url = document.getString("transcriptUrl")
                    if (!url.isNullOrEmpty()) {
                        _existingTranscriptUrl.value = url
                        _uploadStatus.value = "Kayıtlı transkript bulundu."
                    }
                }
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    // GÜNCELLENEN FONKSİYON: Context parametresi eklendi
    fun uploadPdf(context: Context, uri: Uri, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true
        _uploadStatus.value = "Transkript okunuyor..."

        // Arka planda (IO thread) çalıştıralım ki uygulama donmasın
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. ADIM: PDF İÇİNDEKİ YAZIYI OKU (TEXT EXTRACTION)
                val inputStream = context.contentResolver.openInputStream(uri)
                val document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper()
                val extractedText = stripper.getText(document) // İşte transkript metni!
                document.close()
                inputStream?.close()

                // Logcat'e yazdıralım, kontrol et (Logcat'te "TRANSCRIPT_TEXT" diye arat)
                Log.d("TRANSCRIPT_TEXT", extractedText)

                // 2. ADIM: DOSYAYI STORAGE'A YÜKLE
                _uploadStatus.value = "PDF yükleniyor..."
                val fileName = "${UUID.randomUUID()}.pdf"
                val storageRef = storage.reference.child("transcripts/$uid/$fileName")

                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            // 3. ADIM: LİNKİ VE METNİ VERİTABANINA KAYDET
                            saveToFirestore(uid, downloadUri.toString(), extractedText, onSuccess)
                        }
                    }
                    .addOnFailureListener { e ->
                        _loading.value = false
                        _uploadStatus.value = "Yükleme Hatası: ${e.localizedMessage}"
                    }

            } catch (e: Exception) {
                _loading.value = false
                _uploadStatus.value = "Okuma Hatası: ${e.localizedMessage}"
                e.printStackTrace()
            }
        }
    }

    private fun saveToFirestore(uid: String, url: String, text: String, onSuccess: () -> Unit) {
        val updateData = hashMapOf(
            "transcriptUrl" to url,
            "transcriptText" to text // YENİ ALAN: Okunan metni de kaydediyoruz
        )

        db.collection("users").document(uid)
            .update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                _loading.value = false
                _uploadStatus.value = "Transkript Başarıyla Yüklendi ve Analiz Edildi!"
                _existingTranscriptUrl.value = url
                onSuccess()
            }
            .addOnFailureListener { e ->
                _loading.value = false
                _uploadStatus.value = "Veritabanı Hatası: ${e.localizedMessage}"
            }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        onLogout()
    }
}